package com.jong.mikumikureservationbackend.controller.figure;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.figure.doc.FigureControllerDocs;
import com.jong.mikumikureservationbackend.service.figure.FigureService;
import com.jong.mikumikureservationbackend.service.figure.FigureStatusService;
import com.jong.mikumikureservationbackend.vo.figure.response.FigureResponseVO;
import com.jong.mikumikureservationbackend.vo.figure.request.FigureDeleteRequestVO;
import com.jong.mikumikureservationbackend.vo.figure.request.FigureSaveRequestVO;
import com.jong.mikumikureservationbackend.vo.figure.request.FigureStatusRequestVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/figure")
@RequiredArgsConstructor
public class FigureController implements FigureControllerDocs {

    private final FigureService figureService;
    private final FigureStatusService figureStatusService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<List<FigureResponseVO>> getList() {
        return ApiResponse.ok("조회가 완료되었습니다.", figureService.getList(currentUser.getId()));
    }

    @Override
    @GetMapping("/{figureId}")
    public ApiResponse<FigureResponseVO> getDetail(
            @PathVariable Long figureId) {
        return ApiResponse.ok("조회가 완료되었습니다.", figureService.getDetail(currentUser.getId(), figureId));
    }

    @Override
    @PostMapping
    public ApiResponse<FigureResponseVO> create(@Valid @RequestBody FigureSaveRequestVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", figureService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{figureId}")
    public ApiResponse<FigureResponseVO> update(
            @PathVariable Long figureId,
            @Valid @RequestBody FigureSaveRequestVO request) {
        return ApiResponse.ok("수정이 완료되었습니다.", figureService.update(currentUser.getId(), figureId, request));
    }

    @Override
    @PatchMapping("/{figureId}/status")
    public ApiResponse<FigureResponseVO> changeStatus(
            @PathVariable Long figureId,
            @Valid @RequestBody FigureStatusRequestVO request) {
        return ApiResponse.ok("상태 변경이 완료되었습니다.", figureStatusService.change(currentUser.getId(), figureId, request));
    }

    @Override
    @PatchMapping("/status")
    public ApiResponse<List<Long>> changeStatusAll(@Valid @RequestBody FigureStatusRequestVO request) {
        return ApiResponse.ok("상태 변경이 완료되었습니다.", figureStatusService.changeAll(currentUser.getId(), request));
    }

    @Override
    @DeleteMapping("/{figureId}")
    public ApiResponse<Void> delete(
            @PathVariable Long figureId) {
        figureService.delete(currentUser.getId(), figureId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @DeleteMapping
    public ApiResponse<Void> deleteAll(@RequestBody FigureDeleteRequestVO request) {
        figureService.deleteAll(currentUser.getId(), request.getFigureIds());

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }
}
