package com.high.shop.utils;

import com.google.gson.Gson;
import com.high.shop.properies.QiniuCloudProperties;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Component;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Component
public class UploadFileUtils {
    private final QiniuCloudProperties qiniuProperties;

    public UploadFileUtils(QiniuCloudProperties qiniuProperties) {
        this.qiniuProperties = qiniuProperties;
    }

    public String upload(byte[] bytes) {
        return upload(bytes, null);
    }

    public String upload(byte[] bytes, String fileName) {
        //构造一个带指定 Region 对象的配置类
        // Configuration cfg = new Configuration(Region.region0());
        // Region.region2()是华南服务器，region0是华东，region1是华北
        Configuration cfg = new Configuration(Region.region2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        // String accessKey = "your access key";
        // String secretKey = "your secret key";
        // String bucket = "your bucket name";

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        // String key = null;
        String key = fileName;
        try {
            // byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            // Auth auth = Auth.create(accessKey, secretKey);
            Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getAccessSecret());
            // String upToken = auth.uploadToken(bucket);
            String upToken = auth.uploadToken(qiniuProperties.getBucketName());

            try {
                // Response response = uploadManager.put(uploadBytes, key, upToken);
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                // 返回资源域名+资源名称（即上传成功的文件的完整路径）
                return qiniuProperties.getResource() + putRet.key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }
        return null;
    }
}
