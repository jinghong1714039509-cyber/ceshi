package com.rabbiter.association.upload;

import com.rabbiter.association.msg.R;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/uploads")
public class UploadRestController {

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @PostMapping(value = "/covers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadCover(@RequestPart("file") MultipartFile file) throws IOException {
        return R.successData(storeImage(file, "covers"));
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
        return R.successData(storeImage(file, "images"));
    }

    @PostMapping(value = "/avatars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadAvatar(@RequestPart("file") MultipartFile file) throws IOException {
        return R.successData(storeImage(file, "avatars"));
    }

    private Map<String, Object> storeImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("图片文件不能超过 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片格式的文件");
        }

        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        Path uploadDir = resolveUploadRoot().resolve(folder).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = uploadDir.resolve(fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("url", "/uploads/" + folder + "/" + fileName);
        data.put("name", fileName);
        return data;
    }

    private Path resolveUploadRoot() throws IOException {
        Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
        return uploadRoot;
    }

    private String resolveExtension(String originalFileName, String contentType) {
        String ext = StringUtils.getFilenameExtension(originalFileName);
        if (StringUtils.hasText(ext)) {
            return "." + ext.toLowerCase(Locale.ROOT);
        }
        if ("image/png".equals(contentType)) {
            return ".png";
        }
        if ("image/webp".equals(contentType)) {
            return ".webp";
        }
        if ("image/gif".equals(contentType)) {
            return ".gif";
        }
        return ".jpg";
    }
}
