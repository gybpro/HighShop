package com.high.shop.controller;

import com.high.shop.base.BaseManagerController;
import com.high.shop.utils.UploadFileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
public class FileController extends BaseManagerController {
    private final UploadFileUtils uploadFileUtils;

    public FileController(UploadFileUtils uploadFileUtils) {
        this.uploadFileUtils = uploadFileUtils;
    }

    @PostMapping("/admin/file/upload/element")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) throws IOException {
        // 获取文件名
        // String originalFilename = file.getOriginalFilename();

        // 获取后缀名，限制文件上传格式
        // assert originalFilename != null;
        // String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        // 文件格式异常
        // if (StringUtils.isBlank(suffix))
            // 抛异常
        // if (!StringUtils.equals(suffix, "jpg"))
            // 抛异常

        // 获取文件size大小，限制上传文件的大小
        // long mbSize = file.getSize() / 1024 / 1024;

        // if (mbSize > 5)
            // 文件大小超出5兆，抛出异常

        return ok(uploadFileUtils.upload(file.getBytes()));
    }
}
