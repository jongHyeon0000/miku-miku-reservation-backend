package com.jong.mikumikureservationbackend.controller.maker;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.maker.doc.MakerControllerDocs;
import com.jong.mikumikureservationbackend.service.maker.MakerService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.maker.MakerVO;
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
@RequestMapping("/api/maker")
@RequiredArgsConstructor
public class MakerController implements MakerControllerDocs {

    private final MakerService makerService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<PageResponseVO<MakerVO>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", makerService.getPage(currentUser.getId(), page, size));
    }

    @Override
    @PostMapping
    public ApiResponse<MakerVO> create(@Valid @RequestBody MakerVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", makerService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{makerId}")
    public ApiResponse<Void> update(
            @PathVariable Long makerId,
            @Valid @RequestBody MakerVO request) {
        makerService.update(currentUser.getId(), makerId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{makerId}")
    public ApiResponse<Void> delete(
            @PathVariable Long makerId) {
        makerService.delete(currentUser.getId(), makerId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{makerId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long makerId) {
        return ApiResponse.ok("조회가 완료되었습니다.", makerService.getUsage(currentUser.getId(), makerId));
    }
}
