package com.sparta.team6.momo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import com.sparta.team6.momo.model.Plan;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlanRequestDto {

    @Length(max = 20, message = "20자 이내로 입력해주세요")
    @NotBlank(message = "모임 이름을 입력해주세요")
    private String planName;

    @NotBlank(message = "약속 장소를 입력해주세요")
    private String destination;

    @NotNull(message = "위도(lat) 정보가 없습니다")
    private String lat;

    @NotNull(message = "경도(lng) 정보가 없습니다")
    private String lng;

    @Length(max = 50, message = "50자 이내로 입력해주세요")
    private String contents;

    @NotNull(message = "모임 시간을 설정해주세요")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime planDate;

    // 알람시간은 분 단위 String으로 받음
    @Positive
    @NotNull(message = "모두모여 시간을 설정해주세요")
    private Long noticeTime;

    @AssertTrue(message = "모임 시간과 모두모여 시간을 현재 시간 이후로 설정해주세요")
    private boolean isValidDate() {
        return planDate.minusMinutes(noticeTime).isAfter(LocalDateTime.now());
    }

    public LocalDateTime toLocalDateTIme(Long noticeTime) {
        return planDate.minusMinutes(noticeTime);
    }

    public Plan toEntity() {
        return Plan.builder()
                .planName(planName)
                .contents(contents)
                .planDate(planDate)
                .noticeTime(toLocalDateTIme(noticeTime))
                .destination(destination)
                .lat(lat)
                .lng(lng)
                .build();
    }
}
