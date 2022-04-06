package com.sparta.team6.momo.repository;

import com.sparta.team6.momo.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
