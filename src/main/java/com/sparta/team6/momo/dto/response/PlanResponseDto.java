package com.sparta.team6.momo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.sparta.team6.momo.model.Plan;

@Getter
@NoArgsConstructor
public class PlanResponseDto {
    Long planId;
    String lat;
    String lng;

    @Builder
    public PlanResponseDto(Long planId, String lat, String lng) {
        this.planId = planId;
        this.lat = lat;
        this.lng = lng;
    }

    public static PlanResponseDto of(Plan plan) {
        return PlanResponseDto.builder()
                .planId(plan.getId())
                .lat(plan.getLat())
                .lng(plan.getLng())
                .build();
    }
}
