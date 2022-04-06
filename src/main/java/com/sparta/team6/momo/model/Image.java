package com.sparta.team6.momo.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Column
    private String image;

    public Image(Plan plan, String image) {
        this.plan = plan;
        this.image = image;
    }
}
