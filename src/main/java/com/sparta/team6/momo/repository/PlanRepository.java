package com.sparta.team6.momo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.team6.momo.model.Plan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findPlanByUrl(String url);

    List<Plan> findAllByUserIdAndPlanDateBetweenOrderByPlanDateDesc(Long userId, LocalDateTime monthStart, LocalDateTime monthEnd);

    Page<Plan> findAllByUser_Id(Long userId, Pageable pageable);

    @Query(value = "select distinct p from Plan p join fetch p.user u where u.isNoticeAllowed is true and p.noticeTime=:dateTime")
    List<Plan> findAllByNoticeTime(@Param(value = "dateTime") LocalDateTime dateTime);

}
