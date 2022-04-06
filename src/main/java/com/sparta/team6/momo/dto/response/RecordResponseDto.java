package com.sparta.team6.momo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import com.sparta.team6.momo.model.Plan;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RecordResponseDto {
    private Long planId;
    private String planDate;
    private String planName;
    private String destination;
    private boolean finished;

    public RecordResponseDto(Plan plan) {
        this.planId = plan.getId();
        this.planDate = plan.getPlanDate().toString();
        this.planName = plan.getPlanName();
        this.destination = plan.getDestination();
        this.finished = finishCheck(plan.getPlanDate());
    }

    public boolean finishCheck(LocalDateTime planDate) {
        return LocalDateTime.now().isAfter(planDate.plusHours(1));
    }
}
