package com.tencent.wework;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.config.ChatRsaKeyConfig;
import com.easyink.common.config.CosConfig;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.conversation.ChatBodyVO;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.FinanceResVO;
import com.easyink.common.core.domain.conversation.msgtype.*;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.common.utils.uuid.IdUtils;
import com.easyink.common.utils.wecom.RsaUtil;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author admin
 * @description
 * @date 2020/12/2 16:01
 **/
@Slf4j
public class FinanceUtils {
    /**
     * NewSdk返回的sdk指针map
     */
    private static ConcurrentHashMap<String, Long> sdkmap = new ConcurrentHashMap<>();
    /**
     * 超时时间，单位秒
     */
    private static final long TIMEOUT = 5 * 60L;

    private static String downloadWeWorkPath = RuoYiConfig.getDownloadWeWorkPath();

    private static final String CONTENT = "content";

    private FinanceUtils() {
    }

    /**
     * 初始化
     *
     * @param corpId 企业id
     * @param secret 会话存档密钥
     */
    public static void initSDK(String corpId, String secret) {
        Long sdk = sdkmap.get(corpId);
        if (ObjectUtils.isEmpty(sdk) || sdk == 0) {
            sdk = Finance.NewSdk();
            Finance.Init(sdk, corpId, secret);
            sdkmap.put(corpId, sdk);
        }
    }


    private static long getSdk(String corpId) {
        Long sdk = sdkmap.get(corpId);
        if (ObjectUtils.isEmpty(sdk) || sdk == 0) {
            return 0;
        }
        return sdk.longValue();
    }

    /**
     * 拉取聊天记录
     *
     * @param seq    消息的seq值，标识消息的序号
     * @param proxy  代理
     * @param passwd 密码
     */
    public static List<ChatInfoVO> getChatData(long seq, String proxy, String passwd, RedisCache redisCache, String corpId) {
        if (StringUtils.isEmpty(corpId)) {
            return new ArrayList<>();
        }

        List<ChatInfoVO> resList = new ArrayList<>();
        long slice = Finance.NewSlice();
        int ret = Finance.GetChatData(getSdk(corpId), seq, WeConstans.LIMIT, proxy, passwd, TIMEOUT, slice);
        if (ret != 0) {
            log.info("getChatData, corpId: {}, ret :{}", corpId, ret);
            Finance.FreeSlice(slice);
            return new ArrayList<>();
        }
        // 获取返回的消息字符串
        String content = Finance.GetContentFromSlice(slice);
        FinanceResVO result = JSONObject.parseObject(content, FinanceResVO.class);
        if (result.getErrcode() != 0) {
            log.error("同步消息错误:{}, corpId:{}, errmsg:{}", result.getErrcode(), corpId, result.getErrmsg());
            return new ArrayList<>();
        }
        // 获取解密消息需要的字段
        List<FinanceResVO.EncryptVO> chatdataArr = result.getChatdata();
        log.info("corpId:{}开始执行数据解析{}条:------------", corpId, chatdataArr.size());
        AtomicLong localSeq = new AtomicLong(seq);
        if (CollUtil.isNotEmpty(chatdataArr)) {
            chatdataArr.forEach(data -> {
                try {
                    localSeq.set(data.getSeq());
                    ChatRsaKeyConfig chatRsaKeyConfig = SpringUtils.getBean(ChatRsaKeyConfig.class);
                    // 解密不同消息类型并转译成实体
                    ChatInfoVO chatInfoVO = decryptChatRecord(getSdk(corpId), data.getEncrypt_random_key(),
                            data.getEncrypt_chat_msg(), chatRsaKeyConfig.getPrivateKey(), corpId);
                    if (chatInfoVO == null) {
                        return;
                    }
                    chatInfoVO.setSeq(localSeq.get());
                    resList.add(chatInfoVO);
                } catch (Exception e) {
                    log.warn("解析消息出现异常,corpId:{},e:{}", corpId, ExceptionUtils.getStackTrace(e));
                }
            });

            log.info("corpId:{}数据解析完成:------------", corpId);
        }
        Finance.FreeSlice(slice);
        redisCache.setCacheObject(WeConstans.getContactSeqKey(corpId), localSeq.get());
        return resList;
    }

    /**
     * @param sdk             初始化时候获取到的值
     * @param ncryptRandomKey 企业微信返回的随机密钥
     * @param encryptChatMsg  企业微信返回的单条记录的密文消息
     * @param privateKey      企业微信管理后台设置的私钥,!!!版本记得对应上!!!
     * @param corpId          企业id
     * @return JSONObject 返回不同格式的聊天数据,格式有二十来种
     * 详情请看官网 https://open.work.weixin.qq.com/api/doc/90000/90135/91774#%E6%B6%88%E6%81%AF%E6%A0%BC%E5%BC%8F
     */
    private static ChatInfoVO decryptChatRecord(Long sdk, String ncryptRandomKey, String encryptChatMsg, String privateKey, String corpId) {
        Long msg = null;
        try {
            //获取私钥
            PrivateKey privateKeyObj = RsaUtil.getPrivateKey(privateKey);
            String str = RsaUtil.decryptRSA(ncryptRandomKey, privateKeyObj);
            //初始化参数slice
            msg = Finance.NewSlice();

            //解密
            Finance.DecryptData(sdk, str, encryptChatMsg, msg);
            String jsonDataStr = Finance.GetContentFromSlice(msg);
            ChatInfoVO chatInfoVO = JSON.parseObject(jsonDataStr, ChatInfoVO.class);
            String msgType = chatInfoVO.getMsgtype();
            if (StringUtils.isNotEmpty(msgType)) {
                getSwitchType(chatInfoVO, msgType, corpId);
            }
            log.info("数据解析:------------{}", JSONObject.toJSONString(chatInfoVO));
            return chatInfoVO;
        } catch (Exception e) {
            log.error("解析密文失败:{}", ExceptionUtils.getStackTrace(e));
            return null;
        } finally {
            if (msg != null) {
                //释放参数slice
                Finance.FreeSlice(msg);
            }
        }
    }

    /**
     * 根据消息类型处理数据
     *
     * @param realData
     * @param msgType  消息类型
     * @param corpId   企业id
     */
    private static void getSwitchType(ChatBodyVO realData, String msgType, String corpId) {
        switch (msgType) {
            case "ChatRecordText":
            case "text":
                setMediaTextData(realData);
                break;
            case "ChatRecordImage":
            case "image":
                setMediaImageData(realData, msgType, corpId);
                break;
            case "voice":
                setMediaVoiceData(realData, msgType, corpId);
                break;
            case "ChatRecordVideo":
            case "video":
                setMediaVideoData(realData, msgType, corpId);
                break;
            case "emotion":
                setMediaEmotionData(realData, msgType, corpId);
                break;
            case "ChatRecordFile":
            case "file":
                setMediaFileData(realData, msgType, corpId);
                break;
            case "card":
                cardMsgHandler(realData, corpId);
                break;
            case "ChatRecordMixed":
            case "mixed":
                setMediaMixedData(realData, corpId);
                break;
            case "meeting_voice_call":
            case "voip_doc_share":
                setMediaMeetingVoiceCallData(realData, msgType, corpId);
                break;
            case "chatrecord":
                chatRecordMsgHandler(realData, corpId);
                break;
            default:
                break;
        }
    }

    /**
     * 处理会话记录消息以及嵌套会话记录消息
     *
     * @param realData 消息体
     * @param corpId   企业id
     */
    private static void chatRecordMsgHandler(ChatBodyVO realData, String corpId) {
        ChatRecordVO chatRecord = realData.getChatRecord();
        if (chatRecord == null) {
            chatRecord = JSON.parseObject(realData.getContent().toString(), ChatRecordVO.class);
        }
        if (chatRecord == null) {
            return;
        }
        List<ChatRecordVO.ChatRecordItem> items = chatRecord.getItem();
        if (CollectionUtils.isNotEmpty(items)) {
            for (ChatRecordVO.ChatRecordItem chatRecordItem : items) {
                getSwitchType(chatRecordItem, chatRecordItem.getType(), corpId);
            }
        }
        // 同步attachment字段
        resetContent(realData, chatRecord);
    }

    /**
     * 处理音频存档消息以及嵌套音频存档消息
     *
     * @param realData 消息体
     * @param msgType  消息类型
     * @param corpId   企业id
     */
    private static void setMediaMeetingVoiceCallData(ChatBodyVO realData, String msgType, String corpId) {
        MeetingVoiceCallVO meetingVoiceCall = realData.getMeetingVoiceCall();
        if (meetingVoiceCall == null) {
            meetingVoiceCall = JSON.parseObject(realData.getContent().toString(), MeetingVoiceCallVO.class);
        }
        for (MeetingVoiceCallVO.DemofiledataVO data : meetingVoiceCall.getDemofiledata()) {
            getPath(data, msgType, data.getFilename(), meetingVoiceCall.getSdkfileid(), corpId);
        }
        // 同步attachment字段
        resetContent(realData, meetingVoiceCall);
    }

    /**
     * 处理名片消息，无嵌套名片消息
     *
     * @param realData 消息体
     * @param corpId   企业id
     */
    private static void cardMsgHandler(ChatBodyVO realData, String corpId) {
        StringUtils.checkCorpId(corpId);
        CardVO card = realData.getCard();
        if (card == null || StringUtils.isEmpty(card.getUserid())) {
            log.error("card-type msg body is empty");
            throw new BaseException("消息处理失败");
        }
        try {
            String userId = card.getUserid();
            int idType = StringUtils.weCustomTypeJudgment(userId);
            String avatar = "";
            String userName = "";
            if (WeConstans.ID_TYPE_EX.equals(idType)) {
                WeCustomerService weCustomerService = SpringUtils.getBean(WeCustomerService.class);
                WeCustomer weCustomer = weCustomerService.selectWeCustomerById(userId, corpId);
                if (weCustomer != null) {
                    avatar = weCustomer.getAvatar();
                    userName = weCustomer.getName();
                }
            } else if (WeConstans.ID_TYPE_USER.equals(idType)) {
                WeUserService weUserService = SpringUtils.getBean(WeUserService.class);
                WeUser weUser = weUserService.selectWeUserById(corpId, userId);
                if (weUser != null) {
                    avatar = weUser.getAvatarMediaid();
                    userName = weUser.getName();
                }
            } else {
                log.info("机器人信息不处理");
            }
            card.setUserName(userName);
            card.setImageUrl(avatar);
        } catch (Exception e) {
            //防止单条名片解析失败导致全部消息处理异常
            log.error("名片消息处理异常:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 处理文件消息以及嵌套文件消息
     *
     * @param realData 消息体
     * @param msgType  消息类型
     * @param corpId   企业id
     */
    private static void setMediaFileData(ChatBodyVO realData, String msgType, String corpId) {
        FileVO file = realData.getFile();
        if (file == null) {
            // ==null则为混合消息的file类型
            file = JSON.parseObject(realData.getContent().toString(), FileVO.class);
        }
        getPath(file, msgType, file.getFilename(), file.getSdkfileid(), corpId);
        // 同步attachment字段
        resetContent(realData, file);
    }

    /**
     * 处理混合消息以及嵌套混合消息
     *
     * @param realData 消息体
     * @param corpId   企业id
     */
    private static void setMediaMixedData(ChatBodyVO realData, String corpId) {
        MixedVO mixed = realData.getMixed();
        if (mixed == null) {
            mixed = JSON.parseObject(realData.getContent().toString(), MixedVO.class);
        }
        List<MixedVO.ItemContext> items = mixed.getItem();
        items.forEach(item -> getSwitchType(item, item.getType(), corpId));
        // 同步attachment字段
        resetContent(realData, mixed);
    }


    /**
     * 处理图片消息以及嵌套图片消息
     *
     * @param realData 消息体
     * @param msgType  消息类型
     * @param corpId   企业id
     */
    private static void setMediaImageData(ChatBodyVO realData, String msgType, String corpId) {
        // 不为null，则外层为该消息类型
        ImageVO image = realData.getImage();
        // 为null，则外层为mixed消息或者chatrecord消息，消息体存在content中
        if (image == null) {
            image = JSON.parseObject(realData.getContent().toString(), ImageVO.class);
        }
        String fileName = IdUtils.simpleUUID() + ".jpg";
        getPath(image, msgType, fileName, image.getSdkfileid(), corpId);
        // 同步attachment字段
        resetContent(realData, image);
    }

    /**
     * 处理音频消息以及嵌套音频消息
     *
     * @param realData
     * @param msgType
     * @param corpId
     */
    private static void setMediaVoiceData(ChatBodyVO realData, String msgType, String corpId) {
        VoiceVO voice = realData.getVoice();
        if (voice == null) {
            voice = JSON.parseObject(realData.getContent().toString(), VoiceVO.class);
        }
        String fileName = IdUtils.simpleUUID() + ".amr";
        getPath(voice, msgType, fileName, voice.getSdkfileid(), corpId);
        // 同步attachment字段
        resetContent(realData, voice);
    }

    /**
     * 处理视频消息以及嵌套视频消息
     *
     * @param realData
     * @param msgType
     * @param corpId
     */
    private static void setMediaVideoData(ChatBodyVO realData, String msgType, String corpId) {
        VideoVO video = realData.getVideo();
        if (video == null) {
            video = JSON.parseObject(realData.getContent().toString(), VideoVO.class);
        }
        String fileName = IdUtils.simpleUUID() + ".mp4";
        getPath(video, msgType, fileName, video.getSdkfileid(), corpId);
        // 同步attachment字段
        resetContent(realData, video);
    }

    /**
     * 处理表情消息以及嵌套表情消息
     *
     * @param realData 消息体
     * @param msgType  消息类型
     * @param corpId   企业id
     */
    private static void setMediaEmotionData(ChatBodyVO realData, String msgType, String corpId) {

        String fileName = "";
        EmotionVO emotion = realData.getEmotion();
        if (emotion == null) {
            emotion = JSON.parseObject(realData.getContent().toString(), EmotionVO.class);
        }
        Integer type = emotion.getType();
        switch (type) {
            case 1:
                fileName = IdUtils.simpleUUID() + ".gif";
                break;
            case 2:
                fileName = IdUtils.simpleUUID() + ".png";
                break;
            default:
                break;
        }
        getPath(emotion, msgType, fileName, emotion.getSdkfileid(), corpId);
        // 同步attachment字段
        resetContent(realData, emotion);
    }


    /**
     * 处理文本消息，以及嵌套文本消息
     *
     * @param realData 消息体
     */
    private static void setMediaTextData(ChatBodyVO realData) {
        // text不为null，则外层为text消息
        TextVO text = realData.getText();
        // text为null，则外层为mixed消息或者chatrecord消息，消息体存在content中
        if (text == null) {
            // 解析成text消息体
            text = JSON.parseObject(realData.getContent().toString(), TextVO.class);
        }
        // 同步attachment字段
        resetContent(realData, text);
    }

    /**
     * 设置content字段同步attachment字段
     *
     * @param realData
     * @param content
     * @param <T>
     */
    private static <T extends AttachmentBaseVO> void resetContent(ChatBodyVO realData, T content) {
        // 如果是混合消息的content，则为object类型，
        if (realData instanceof MixedVO.ItemContext) {
            realData.setContent(content);
        }
        // 如果是会话记录的content，则为keyword类型不能转为object（ES插入会报错）
        if (realData instanceof ChatRecordVO.ChatRecordItem) {
            realData.setContent(JSON.toJSONString(content));
        }

    }

    //云存储
    private static void getPath(AttachmentBaseVO data, String msgType, String fileName, String sdkfileid, String corpId) {
        String filePath = getFilePath(msgType);
        try {
            getMediaData(sdkfileid, "", "", filePath, fileName, corpId);
            RuoYiConfig ruoyiConfig = SpringUtils.getBean(RuoYiConfig.class);
            CosConfig cosConfig = ruoyiConfig.getFile().getCos();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            StringBuilder cosUrl = new StringBuilder(cosConfig.getCosImgUrlPrefix());
            String cosFilePath = FileUploadUtils.upload2Cos(new FileInputStream(new File(filePath, fileName)), fileName, suffix, cosConfig);
            cosUrl.append(cosFilePath);
            data.setAttachment(cosUrl.toString());
        } catch (Exception e) {
            log.error("getPath Exception ex:【{}】", ExceptionUtils.getStackTrace(e));
        }
    }

    private static String getFilePath(String msgType) {
        return StringUtils.format(downloadWeWorkPath, msgType, DateUtils.getDate());
    }

    private static void getMediaData(String sdkFileid, String proxy, String passwd, String filePath, String fileName, String corpId) {
        String indexbuf = "";
        while (true) {
            long mediaData = Finance.NewMediaData();
            int ret = Finance.GetMediaData(getSdk(corpId), indexbuf, sdkFileid, proxy, passwd, TIMEOUT, mediaData);
            log.info("getMediaData ret:" + ret);
            if (ret != 0) {
                return;
            }
            FileOutputStream outputStream = null;
            try {
                File f = new File(filePath);
                if (!f.exists()) {
                    f.mkdirs();
                }
                File file = new File(filePath, fileName);
                if (!file.isDirectory() && !file.createNewFile()) {
                    throw new CustomException("getMediaData 文件不存在，创建文件失败");
                }
                outputStream = new FileOutputStream(file, true);
                outputStream.write(Finance.GetData(mediaData));
                outputStream.close();
            } catch (IOException e) {
                log.error("getMediaData exception ex:【{}】", ExceptionUtils.getStackTrace(e));
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        log.error("getMediaData exception ex:【{}】", ExceptionUtils.getStackTrace(e));
                    }
                }
            }
            if (Finance.IsMediaDataFinish(mediaData) == 1) {
                Finance.FreeMediaData(mediaData);
                break;
            } else {
                indexbuf = Finance.GetOutIndexBuf(mediaData);
                Finance.FreeMediaData(mediaData);
            }
        }
    }

}
