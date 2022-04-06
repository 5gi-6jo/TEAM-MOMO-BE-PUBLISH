package com.sparta.team6.momo.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

import static com.sparta.team6.momo.model.UserRole.ROLE_GUEST;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "guest_id")
public class Guest extends Account {

    public Guest(@NonNull String nickname) {
        super(nickname, ROLE_GUEST);
    }

}
