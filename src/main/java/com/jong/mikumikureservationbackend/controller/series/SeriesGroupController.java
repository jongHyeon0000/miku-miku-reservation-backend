package com.jong.mikumikureservationbackend.controller.series;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.series.doc.SeriesGroupControllerDocs;
import com.jong.mikumikureservationbackend.service.series.SeriesGroupService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesGroupVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/series-group")
@RequiredArgsConstructor
public class SeriesGroupController implements SeriesGroupControllerDocs {

    private final SeriesGroupService seriesGroupService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<List<SeriesGroupVO>> getTree() {
        return ApiResponse.ok("조회가 완료되었습니다.", seriesGroupService.getTree(currentUser.getId()));
    }

    @Override
    @PostMapping
    public ApiResponse<SeriesGroupVO> create(@Valid @RequestBody SeriesGroupVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", seriesGroupService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{seriesGroupId}")
    public ApiResponse<Void> update(
            @PathVariable Long seriesGroupId,
            @Valid @RequestBody SeriesGroupVO request) {
        seriesGroupService.update(currentUser.getId(), seriesGroupId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{seriesGroupId}")
    public ApiResponse<Void> delete(
            @PathVariable Long seriesGroupId) {
        seriesGroupService.delete(currentUser.getId(), seriesGroupId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{seriesGroupId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long seriesGroupId) {
        return ApiResponse.ok("조회가 완료되었습니다.", seriesGroupService.getUsage(currentUser.getId(), seriesGroupId));
    }
}
