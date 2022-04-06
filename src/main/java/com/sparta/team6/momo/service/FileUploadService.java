package com.sparta.team6.momo.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.exception.ErrorCode;
import com.sparta.team6.momo.repository.ImageRepository;
import com.sparta.team6.momo.repository.PlanRepository;
import com.sparta.team6.momo.utils.amazonS3.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.sparta.team6.momo.dto.ImageDto;
import com.sparta.team6.momo.model.Image;
import com.sparta.team6.momo.model.Plan;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final UploadService uploadService;
    private final ImageRepository imageRepository;
    private final PlanRepository planRepository;

    //Multipart를 통해 전송된 파일을 업로드하는 메서드
    @CacheEvict(key = "#planId", value = "images")
    @Transactional
    public List<ImageDto> uploadImage(List<MultipartFile> files, Long planId, Long userId) {
        List<ImageDto> imageDtoList = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            String fileName = createFileName(multipartFile.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getSize());
            // S3 storage에 저장
            try (InputStream inputStream = multipartFile.getInputStream()) {
                uploadService.uploadFile(inputStream, objectMetadata, fileName);
            } catch (IOException e) {
                log.info("이미지 파일 변환 실패");
                throw new CustomException(ErrorCode.FILE_CONVERT_ERROR);
            }
            // DB에 저장
            Optional<Plan> plan = planRepository.findById(planId);
            if (plan.isPresent() && userId.equals(plan.get().getUser().getId())) {
                Image image = new Image(plan.get(), uploadService.getFileUrl(fileName));
                imageRepository.save(image);
                imageDtoList.add(new ImageDto(image));
            } else {
                log.info("조건에 맞는 모임이 존재하지 않습니다");
                throw new CustomException(ErrorCode.PLAN_NOT_FOUND);
            }
        }
        return imageDtoList;
    }

    @Cacheable(key = "#planId", value = "images")
    public List<ImageDto> showImage(Long planId, Long userId) {
        List<Image> imageList = imageRepository.findAllByPlan_Id(planId);
        List<ImageDto> dtoList = new ArrayList<>();
        try {
            if (userId.equals(imageList.get(0).getPlan().getUser().getId())) {
                for (Image image : imageList) {
                    dtoList.add(new ImageDto(image.getId(), image.getImage()));
                }
                return dtoList;
            }
            log.info("Account 정보가 일치하지 않습니다");
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        } catch (IndexOutOfBoundsException e) {
            return dtoList;
        }
    }

    @CacheEvict(key = "#planId", value = "images")
    public void deleteImageS3(Long planId, Long imageId, Long userId) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isPresent() && userId.equals(image.get().getPlan().getUser().getId())) {
            uploadService.deleteFile(image.get().getImage().split(".com/")[1]);
            imageRepository.deleteById(imageId);
            return;
        }
        log.info("해당 이미지가 존재하지 않습니다");
        throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
    }

    // 기존 확장자명을 유지한 채, 유니크한 파일의 이름을 생성하는 메서드
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    // 파일의 확장자명을 가져오는 메서드
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (Exception e) {
            log.info("잘못된 형식의 확장자입니다");
            throw new CustomException(ErrorCode.INVALID_FILE_FORMAT);
        }
    }
}
