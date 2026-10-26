package com.jong.mikumikureservationbackend.controller.type;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.type.doc.FigureTypeGroupControllerDocs;
import com.jong.mikumikureservationbackend.service.type.FigureTypeGroupService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeGroupVO;
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
@RequestMapping("/api/figure-type-group")
@RequiredArgsConstructor
public class FigureTypeGroupController implements FigureTypeGroupControllerDocs {

    private final FigureTypeGroupService figureTypeGroupService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<List<FigureTypeGroupVO>> getTree() {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeGroupService.getTree(currentUser.getId()));
    }

    @Override
    @PostMapping
    public ApiResponse<FigureTypeGroupVO> create(@Valid @RequestBody FigureTypeGroupVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", figureTypeGroupService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{figureTypeGroupId}")
    public ApiResponse<Void> update(
            @PathVariable Long figureTypeGroupId,
            @Valid @RequestBody FigureTypeGroupVO request) {
        figureTypeGroupService.update(currentUser.getId(), figureTypeGroupId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{figureTypeGroupId}")
    public ApiResponse<Void> delete(
            @PathVariable Long figureTypeGroupId) {
        figureTypeGroupService.delete(currentUser.getId(), figureTypeGroupId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{figureTypeGroupId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long figureTypeGroupId) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeGroupService.getUsage(currentUser.getId(), figureTypeGroupId));
    }
}
