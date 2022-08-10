package com.tencent.wework;


import com.easyink.common.utils.OsUtils;

public class Finance {
    public static native long NewSdk();

    private Finance() {
    }

    /**
     * 初始化函数
     * Return值=0表示该API调用成功
     *
     * @param /[in] sdk         NewSdk返回的sdk指针
     * @param /[in] corpid      调用企业的企业id，例如：wwd08c8exxxx5ab44d，可以在企业微信管理端--我的企业--企业信息查看
     * @param /[in] secret      聊天内容存档的Secret，可以在企业微信管理端--管理工具--聊天内容存档查看
     * @return 返回是否初始化成功
     * 0   - 成功
     * !=0 - 失败
     */
    public static native int Init(long sdk, String corpid, String secret);

    /**
     * 拉取聊天记录函数
     * Return值=0表示该API调用成功
     *
     * @param /[in]  sdk             NewSdk返回的sdk指针
     * @param /[in]  seq             从指定的seq开始拉取消息，注意的是返回的消息从seq+1开始返回，seq为之前接口返回的最大seq值。首次使用请使用seq:0
     * @param /[in]  limit           一次拉取的消息条数，最大值1000条，超过1000条会返回错误
     * @param /[in]  proxy           使用代理的请求，需要传入代理的链接。如：socks5://10.0.0.1:8081 或者 http://10.0.0.1:8081
     * @param /[in]  passwd          代理账号密码，需要传入代理的账号密码。如 user_name:passwd_123
     * @param /[in]  timeout         超时时间，单位秒
     * @param /[out] chatDatas       返回本次拉取消息的数据，slice结构体.内容包括errcode/errmsg，以及每条消息内容。
     * @return 返回是否调用成功
     * 0   - 成功
     * !=0 - 失败
     */
    public static native int GetChatData(long sdk, long seq, long limit, String proxy, String passwd, long timeout, long chatData);

    /**
     * 拉取媒体消息函数
     * Return值=0表示该API调用成功
     *
     * @param /[in]  sdk             NewSdk返回的sdk指针
     * @param /[in]  sdkFileid       从GetChatData返回的聊天消息中，媒体消息包括的sdkfileid
     * @param /[in]  proxy           使用代理的请求，需要传入代理的链接。如：socks5://10.0.0.1:8081 或者 http://10.0.0.1:8081
     * @param /[in]  passwd          代理账号密码，需要传入代理的账号密码。如 user_name:passwd_123
     * @param /[in]  timeout         超时时间，单位秒
     * @param /[in]  indexbuf        媒体消息分片拉取，需要填入每次拉取的索引信息。首次不需要填写，默认拉取512k，后续每次调用只需要将上次调用返回的outindexbuf填入即可。
     * @param /[out] media_data      返回本次拉取的媒体数据.MediaData结构体.内容包括data(数据内容)/outindexbuf(下次索引)/is_finish(拉取完成标记)
     * @return 返回是否调用成功
     * 0   - 成功
     * !=0 - 失败
     */
    public static native int GetMediaData(long sdk, String indexbuf, String sdkField, String proxy, String passwd, long timeout, long mediaData);

    /**
     * @param /[in]  encrypt_key, getchatdata返回的encrypt_key
     * @param /[in]  encrypt_msg, getchatdata返回的content
     * @param /[out] msg, 解密的消息明文
     * @return 返回是否调用成功
     * 0   - 成功
     * !=0 - 失败
     * @brief 解析密文
     */
    public static native int DecryptData(long sdk, String encrypt_key, String encrypt_msg, long msg);

    public static native void DestroySdk(long sdk);

    public static native long NewSlice();

    /**
     * @return
     * @brief 释放slice，和NewSlice成对使用
     */
    public static native void FreeSlice(long slice);

    /**
     * @return 内容
     * @brief 获取slice内容
     */
    public static native String GetContentFromSlice(long slice);

    /**
     * @return 内容
     * @brief 获取slice内容长度
     */
    public static native int GetSliceLen(long slice);

    public static native long NewMediaData();

    public static native void FreeMediaData(long mediaData);

    /**
     * @return outindex
     * @brief 获取mediadata outindex
     */
    public static native String GetOutIndexBuf(long mediaData);

    /**
     * @return data
     * @brief 获取mediadata data数据
     */
    public static native byte[] GetData(long mediaData);

    public static native int GetIndexLen(long mediaData);

    public static native int GetDataLen(long mediaData);

    /**
     * @return 1完成、0未完成
     * @brief 判断mediadata是否结束
     */
    public static native int IsMediaDataFinish(long mediaData);

    static {
        if (OsUtils.isWindows()) {
            System.loadLibrary("WeWorkFinanceSdk");
        } else {
            System.load("/usr/lib/libWeWorkFinanceSdk_Java.so");
        }

    }


}