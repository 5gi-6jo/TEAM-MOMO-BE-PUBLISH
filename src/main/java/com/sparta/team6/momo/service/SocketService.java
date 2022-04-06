package com.sparta.team6.momo.service;

import com.sparta.team6.momo.dto.MapDto;
import com.sparta.team6.momo.exception.CustomException;
import com.sparta.team6.momo.exception.ErrorCode;
import com.sparta.team6.momo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sparta.team6.momo.model.Plan;

@Service
@RequiredArgsConstructor
public class SocketService {

    private final PlanRepository planRepository;

    public void setDestination(Long planId, MapDto mapDto) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        mapDto.setDestLat(plan.getLat());
        mapDto.setDestLng(plan.getLng());
    }
}
