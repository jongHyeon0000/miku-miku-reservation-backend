package com.jong.mikumikureservationbackend.controller.series;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.series.doc.SeriesControllerDocs;
import com.jong.mikumikureservationbackend.service.series.SeriesService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesDeleteRequestVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController implements SeriesControllerDocs {

    private final SeriesService seriesService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<PageResponseVO<SeriesVO>> getPage(
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", seriesService.getPage(currentUser.getId(), groupId, page, size));
    }

    @Override
    @PostMapping
    public ApiResponse<SeriesVO> create(@Valid @RequestBody SeriesVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", seriesService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{seriesId}")
    public ApiResponse<Void> update(
            @PathVariable Long seriesId,
            @Valid @RequestBody SeriesVO request) {
        seriesService.update(currentUser.getId(), seriesId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{seriesId}")
    public ApiResponse<Void> delete(
            @PathVariable Long seriesId) {
        seriesService.delete(currentUser.getId(), seriesId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @DeleteMapping
    public ApiResponse<Void> deleteAll(@RequestBody SeriesDeleteRequestVO request) {
        seriesService.deleteAll(currentUser.getId(), request.getSeriesIds());

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{seriesId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long seriesId) {
        return ApiResponse.ok("조회가 완료되었습니다.", seriesService.getUsage(currentUser.getId(), seriesId));
    }
}
