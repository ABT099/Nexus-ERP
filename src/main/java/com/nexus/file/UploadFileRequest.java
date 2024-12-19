package com.nexus.file;

import org.springframework.web.multipart.MultipartFile;

public record UploadFileRequest(
        MultipartFile file,
        String name,
        String description,
        Integer projectId
) {
}
