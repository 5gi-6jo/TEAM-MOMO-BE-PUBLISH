package com.sparta.team6.momo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.team6.momo.model.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPlan_Id(Long id);
    List<Image> deleteAllByPlanId(Long id);
}
