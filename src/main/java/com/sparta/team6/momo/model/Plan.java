package com.sparta.team6.momo.model;

import com.sparta.team6.momo.dto.request.PlanRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(nullable = false)
    private String planName;

    @Column
    private String contents;

    @Column(nullable = false)
    private LocalDateTime planDate;

    @Column(nullable = false)
    private LocalDateTime noticeTime;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String lat;

    @Column(nullable = false)
    private String lng;

    @Column
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Plan(String planName, String contents, LocalDateTime planDate, LocalDateTime noticeTime, String destination, String lat, String lng) {
        this.planName = planName;
        this.contents = contents;
        this.planDate = planDate;
        this.noticeTime = noticeTime;
        this.destination = destination;
        this.lat = lat;
        this.lng = lng;
    }

    public void update(PlanRequestDto requestDto) {
        this.planName = requestDto.getPlanName();
        this.contents = requestDto.getContents();
        this.planDate = requestDto.getPlanDate();
        this.noticeTime = requestDto.toLocalDateTIme(requestDto.getNoticeTime());
        this.destination = requestDto.getDestination();
        this.lat = requestDto.getLat();
        this.lng = requestDto.getLng();
    }

    public void addPlan(User user) {
        this.user = user;
        user.getPlanList().add(this);
    }

    public void addUrl(String url) {
        this.url = url;
    }
}
