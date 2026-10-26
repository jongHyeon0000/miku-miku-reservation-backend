package com.jong.mikumikureservationbackend.controller.type;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.type.doc.FigureTypeControllerDocs;
import com.jong.mikumikureservationbackend.service.type.FigureTypeService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeVO;
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
@RequestMapping("/api/figure-type")
@RequiredArgsConstructor
public class FigureTypeController implements FigureTypeControllerDocs {

    private final FigureTypeService figureTypeService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<PageResponseVO<FigureTypeVO>> getPage(
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeService.getPage(currentUser.getId(), groupId, page, size));
    }

    @Override
    @PostMapping
    public ApiResponse<FigureTypeVO> create(@Valid @RequestBody FigureTypeVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", figureTypeService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{figureTypeId}")
    public ApiResponse<Void> update(
            @PathVariable Long figureTypeId,
            @Valid @RequestBody FigureTypeVO request) {
        figureTypeService.update(currentUser.getId(), figureTypeId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{figureTypeId}")
    public ApiResponse<Void> delete(
            @PathVariable Long figureTypeId) {
        figureTypeService.delete(currentUser.getId(), figureTypeId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{figureTypeId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long figureTypeId) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureTypeService.getUsage(currentUser.getId(), figureTypeId));
    }
}
