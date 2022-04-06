package com.sparta.team6.momo.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Account extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column
    private String token;

    public Account(@NonNull String nickname, @NonNull UserRole userRole) {
        this.nickname = nickname;
        this.userRole = userRole;
    }


    public void updateToken(String token) {
        this.token = token;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
