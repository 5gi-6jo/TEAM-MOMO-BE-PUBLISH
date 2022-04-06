package com.sparta.team6.momo.controller;

import com.sparta.team6.momo.annotation.DTOValid;
import com.sparta.team6.momo.dto.request.PlanRequestDto;
import com.sparta.team6.momo.dto.response.DetailResponseDto;
import com.sparta.team6.momo.dto.response.MainResponseDto;
import com.sparta.team6.momo.dto.response.PlanResponseDto;
import com.sparta.team6.momo.dto.response.Success;
import com.sparta.team6.momo.service.PlanService;
import com.sparta.team6.momo.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;
    private final AccountUtils accountUtils;

    //    /plans?date=2022-03-29T00:00:00
    @GetMapping
    public ResponseEntity<Success<List<MainResponseDto>>> showMain(@RequestParam("date") String date) {
        List<MainResponseDto> dtoList = planService.showMain(date, accountUtils.getCurUserId());
        log.info("모임 리스트(월) 조회 성공");
        return ResponseEntity.ok().body(new Success<>("조회 완료", dtoList));
    }

    /* @Valid 파라미터 바로 뒤에 무조건 BindingResult 파라미터가 위치해야함 */
    @DTOValid
    @PostMapping
    public ResponseEntity<Success<Long>> makePlan(@Valid @RequestBody PlanRequestDto requestDto, BindingResult bindingResult) {
        Long planId = planService.savePlan(requestDto, accountUtils.getCurUserId());
        log.info("모임 생성 성공");
        return ResponseEntity.ok().body(new Success<>("생성 완료", planId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<Success<DetailResponseDto>> showDetail(@PathVariable Long planId) {
        DetailResponseDto responseDto = planService.showDetail(planId, accountUtils.getCurUserId());
        log.info("모임 세부 조회 성공");
        return ResponseEntity.ok().body(new Success<>("조회 완료", responseDto));
    }

    @DTOValid
    @PutMapping("/{planId}")
    public ResponseEntity<Success<PlanResponseDto>> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody PlanRequestDto requestDto,
            BindingResult bindingResult) {
        PlanResponseDto responseDto = planService.updatePlan(planId, requestDto, accountUtils.getCurUserId());
        log.info("모임 수정 성공");
        return ResponseEntity.ok().body(new Success<>("수정 완료", responseDto));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Success<Object>> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId, accountUtils.getCurUserId());
        log.info("모임 삭제 성공");
        return ResponseEntity.ok().body(new Success<>("삭제 완료"));
    }

}
