package com.easywecom.common.utils.file;

import com.easywecom.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ws.schild.jave.MultimediaObject;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件处理工具类
 *
 * @author admin
 */
@Slf4j
public class FileUtils extends org.apache.commons.io.FileUtils {
    public static final String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    log.error("异常信息 e:{}", e1.getMessage());
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    log.error("异常信息 e:{}", e1.getMessage());
                }
            }
        }
    }

    /**
     * 批量下载文件，打包为zip。
     *
     * @param fileList 文件列表，每个元素应包含fileName、url，前者即为文件名，后者为下载所需链接
     * @param os       输出流
     */
    public static void batchDownloadFile(List<Map<String, String>> fileList, OutputStream os) {
        try {
            ZipOutputStream zos = new ZipOutputStream(os);
            for (Map<String, String> fileInfo : fileList) {
                String fileName = fileInfo.get("fileName");
                String url = fileInfo.get("url");
                // 跳过不包含指定键位的对象
                if (StringUtils.isEmpty(url) || StringUtils.isEmpty(fileName)) {
                    continue;
                }
                URL newUrl = new URL(url);
                zos.putNextEntry(new ZipEntry(fileName));
                InputStream fis = newUrl.openConnection().getInputStream();
                byte[] buffer = new byte[1024];
                int r = 0;
                while ((r = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, r);
                }
                fis.close();
            }
            //关闭zip输出流
            zos.flush();
            zos.close();
        } catch (IOException e) {
            log.error("输入输出异常 ex:【{}】", ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * @param urlPath 下载路径
     * @param os      输出流
     * @return 返回下载文件
     * @throws Exception
     */
    public static void downloadFile(String urlPath, OutputStream os) {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET（对于知识库的附件服务器必须是GET，如果是POST会返回405。流程附件迁移功能里面必须是POST，有所区分。）
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.getResponseCode();

            inputStream = httpURLConnection.getInputStream();

            byte[] b = new byte[1024];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            inputStream.close();
            os.close();
        } catch (IOException e) {
            log.error("输入输出异常 ex:【{}】", ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            log.error("downloadFile Exception ex:【{}】", ExceptionUtils.getStackTrace(e));
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    log.error("异常信息:{}", e1.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    log.error("异常信息:{}", e1.getMessage());
                }
            }
        }
    }


    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName)
            throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        String utf8 = "utf-8";
        String firefox = "Firefox";
        String chrome = "Chrome";
        String charsetName = "ISO8859-1";
        String msie = "MSIE";
        if (agent.contains(msie)) {
            // IE浏览器
            filename = URLEncoder.encode(filename, utf8);
            filename = filename.replace("+", " ");
        } else if (agent.contains(firefox)) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), charsetName);
        } else if (agent.contains(chrome)) {
            // google浏览器
            filename = URLEncoder.encode(filename, utf8);
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, utf8);
        }
        return filename;
    }

    /**
     * 获取网络文件，暂存为临时文件
     *
     * @param url url
     * @return 临时文件
     * @throws UnknownHostException
     * @throws IOException
     */
    public static File getFileByUrl(String url) throws UnknownHostException, IOException {
        //创建临时文件
        File tmpFile = File.createTempFile("temp", ".tmp");
        toLocalFile(url, tmpFile.getCanonicalPath());
        return tmpFile;
    }

    /**
     * 网络文件转换为本地文件
     *
     * @param urlStr   url
     * @param localUrl 本地路径
     * @throws IOException
     * @throws UnknownHostException
     */
    private static void toLocalFile(String urlStr, String localUrl) throws IOException, UnknownHostException {
        byte[] data = toByteArray(urlStr);
        FileOutputStream out = new FileOutputStream(localUrl);
        out.write(data);
        out.close();
    }

    /**
     * 网络文件转换为byte二进制
     *
     * @param urlStr
     * @return
     * @throws IOException
     */
    private static byte[] toByteArray(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        DataInputStream in = new DataInputStream(conn.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * 获取时长
     *
     * @param file 文件路径
     * @return 时长(秒)
     * @throws ws.schild.jave.EncoderException
     */
    public static long getDuration(File file) throws ws.schild.jave.EncoderException {
        MultimediaObject multimediaObject = new MultimediaObject(file);
        return multimediaObject.getInfo().getDuration() / 1000;
    }

}
