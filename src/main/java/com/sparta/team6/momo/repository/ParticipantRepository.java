package com.sparta.team6.momo.repository;

import com.sparta.team6.momo.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
