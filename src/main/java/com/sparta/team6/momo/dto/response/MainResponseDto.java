package com.sparta.team6.momo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.sparta.team6.momo.model.Plan;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MainResponseDto {
    private Long planId;
    private String planName;
    private String planDate;
    private String noticeTime;
    private String finishTime;
    private String url;
    private boolean finished;

    public MainResponseDto(Plan plan) {
        this.planId = plan.getId();
        this.planName = plan.getPlanName();
        this.planDate = plan.getPlanDate().toString();
        this.noticeTime = plan.getNoticeTime().toString();
        this.finishTime = plan.getPlanDate().plusHours(1).toString();
        this.url = activeCheck(plan.getNoticeTime(), plan.getPlanDate().plusHours(1), plan.getUrl());
        this.finished = finishCheck(plan.getPlanDate());
    }

    private boolean finishCheck(LocalDateTime planDate) {
        return LocalDateTime.now().isAfter(planDate.plusHours(1));
    }

    private String activeCheck(LocalDateTime noticeTime, LocalDateTime finishTime, String url) {
        if (LocalDateTime.now().isAfter(noticeTime) && LocalDateTime.now().isBefore(finishTime)) {
            return url;
        }
        return null;
    }

}
