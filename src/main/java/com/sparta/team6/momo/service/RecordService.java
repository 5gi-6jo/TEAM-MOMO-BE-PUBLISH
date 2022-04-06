package com.sparta.team6.momo.service;

import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.sparta.team6.momo.dto.response.RecordResponseDto;
import com.sparta.team6.momo.model.Plan;
import com.sparta.team6.momo.repository.PlanRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {

    private static final int PAGE_SIZE = 100;

    private final PlanRepository planRepository;

    @Cacheable(key = "#userId", value = "records")
    public List<RecordResponseDto> showRecord(Long pageNumber, Long userId) {
        Pageable pageRequest = PageRequest.of(pageNumber.intValue(), PAGE_SIZE, Sort.by("planDate", "createdAt").descending());
        Page<Plan> planList = planRepository.findAllByUser_Id(userId, pageRequest);

        if (planList.getTotalPages() <= pageNumber) {
            log.info("모임 정보가 존재하지 않습니다(마지막 페이지)");
            throw new CustomException(ErrorCode.DO_NOT_HAVE_ANY_RESOURCE);
        }
        List<RecordResponseDto> dtoList = new ArrayList<>();
        for (Plan plan : planList) {
            dtoList.add(new RecordResponseDto(plan));
        }
        return dtoList;
    }
}
