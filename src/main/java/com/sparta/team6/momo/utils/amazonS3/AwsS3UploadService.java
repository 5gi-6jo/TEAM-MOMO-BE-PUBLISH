package com.sparta.team6.momo.utils.amazonS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3UploadService implements UploadService {

    private final AmazonS3 amazonS3;
    private final S3Component component;

    @Override
    public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {
        amazonS3.putObject(new PutObjectRequest(component.getBucket(), fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("awsS3 이미지 업로드 성공");
    }

    @Override
    public String getFileUrl(String fileName) {
        return amazonS3.getUrl(component.getBucket(), fileName).toString();
    }

    @Override
    public void deleteFile(String fileName) {
//        DeleteObjectRequest request = new DeleteObjectRequest(component.getBucket(), fileName);
//        amazonS3.deleteObject(request);
        DeleteObjectsRequest request = new DeleteObjectsRequest(component.getBucket()).withKeys(fileName);
        amazonS3.deleteObjects(request);
        log.info("awsS3 이미지 삭제 성공");
    }

}
