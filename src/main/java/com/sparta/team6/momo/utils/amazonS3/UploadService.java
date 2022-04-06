package com.sparta.team6.momo.utils.amazonS3;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

public interface UploadService {
    void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName);

    String getFileUrl(String fileName);

    void deleteFile(String fileName);
}
