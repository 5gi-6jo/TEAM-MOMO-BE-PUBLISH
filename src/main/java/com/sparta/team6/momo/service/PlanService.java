package com.sparta.team6.momo.service;

import com.sparta.team6.momo.dto.*;
import com.sparta.team6.momo.dto.request.PlanRequestDto;
import com.sparta.team6.momo.dto.response.DetailResponseDto;
import com.sparta.team6.momo.dto.response.MainResponseDto;
import com.sparta.team6.momo.dto.response.PlanResponseDto;
import com.sparta.team6.momo.model.User;
import com.sparta.team6.momo.utils.amazonS3.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.exception.ErrorCode;
import com.sparta.team6.momo.model.Image;
import com.sparta.team6.momo.model.Plan;
import com.sparta.team6.momo.repository.UserRepository;
import com.sparta.team6.momo.repository.ImageRepository;
import com.sparta.team6.momo.repository.PlanRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sparta.team6.momo.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.sparta.team6.momo.exception.ErrorCode.PLAN_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {


    private final PlanRepository planRepository;
    private final ImageRepository imageRepository;
    private final UploadService uploadService;
    private final UserRepository userRepository;

    @CacheEvict(key = "#userId", value = {"plans", "records"})
    @Transactional
    public Long savePlan(PlanRequestDto request, Long userId) {

        Plan savedPlan = planRepository.save(request.toEntity());
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.info("해당 Account 정보가 존재하지 않습니다");
                    throw new CustomException(MEMBER_NOT_FOUND);
                });
        savedPlan.addPlan(user);
        savedPlan.addUrl(UUID.randomUUID().toString());
        return savedPlan.getId();
    }

    @CacheEvict(key = "#userId", value = {"plans", "records", "single-plan"})
    @Transactional
    public void deletePlan(Long planId, Long userId) {
        Plan result = planRepository.findById(planId).orElseThrow(
                () -> {
                    log.info("해당 모임 정보가 존재하지 않습니다");
                    throw new CustomException(PLAN_NOT_FOUND);
                });
        if (userId.equals(result.getUser().getId())) {
            List<Image> imageList = imageRepository.deleteAllByPlanId(planId);
            for (Image image : imageList) {
                uploadService.deleteFile(image.getImage().split(".com/")[1]);
            }
            planRepository.deleteById(planId);
        } else {
            log.info("Account 정보가 일치하지 않습니다");
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
    }

    @CacheEvict(key = "#userId", value = {"plans", "records", "single-plan"})
    @Transactional
    public PlanResponseDto updatePlan(Long planId, PlanRequestDto requestDto, Long userId) {
        Plan savedPlan = planRepository.findById(planId).orElseThrow(
                () -> {
                    log.info("해당 모임 정보가 존재하지 않습니다");
                    throw new CustomException(PLAN_NOT_FOUND);
                });
        if (userId.equals(savedPlan.getUser().getId())) {
            savedPlan.update(requestDto);
            return PlanResponseDto.of(savedPlan);
        } else {
            log.info("Account 정보가 일치하지 않습니다");
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
    }

    public DetailResponseDto showDetail(Long planId, Long userId) {
        Plan plan = planRepository.findById(planId).orElseThrow(
                () -> {
                    log.info("해당 모임 정보가 존재하지 않습니다");
                    throw new CustomException(PLAN_NOT_FOUND);
                });
        if (userId.equals(plan.getUser().getId())) {
            List<Image> imageList = imageRepository.findAllByPlan_Id(planId);
            List<ImageDto> imageDtoList = new ArrayList<>();
            for (Image image : imageList) {
                imageDtoList.add(new ImageDto(image.getId(), image.getImage()));
            }
            return DetailResponseDto.of(plan, imageDtoList);
        }
        log.info("Account 정보가 일치하지 않습니다");
        throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
    }

    @Cacheable(key = "#userId", value = "plans")
    public List<MainResponseDto> showMain(String date, Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter).minusMonths(1);
        LocalDateTime monthStart = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), 1, 0, 0, 0);
        LocalDateTime monthEnd = monthStart.plusMonths(2).minusSeconds(1);
        List<Plan> planList = planRepository.findAllByUserIdAndPlanDateBetweenOrderByPlanDateDesc(userId, monthStart, monthEnd);
//        List<Plan> planList = planRepository.findAllByUserIdAndPlanDateBetween(userId, monthStart, monthEnd);
        List<MainResponseDto> dtoList = new ArrayList<>();
        for (Plan plan : planList) {
            dtoList.add(new MainResponseDto(plan));
        }
        return dtoList;
    }
}
