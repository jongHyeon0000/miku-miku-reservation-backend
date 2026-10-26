package com.jong.mikumikureservationbackend.controller.shop;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.shop.doc.ShopGroupControllerDocs;
import com.jong.mikumikureservationbackend.service.shop.ShopGroupService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopGroupVO;
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
@RequestMapping("/api/shop-group")
@RequiredArgsConstructor
public class ShopGroupController implements ShopGroupControllerDocs {

    private final ShopGroupService shopGroupService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<List<ShopGroupVO>> getTree() {
        return ApiResponse.ok("조회가 완료되었습니다.", shopGroupService.getTree(currentUser.getId()));
    }

    @Override
    @PostMapping
    public ApiResponse<ShopGroupVO> create(@Valid @RequestBody ShopGroupVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", shopGroupService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{shopGroupId}")
    public ApiResponse<Void> update(
            @PathVariable Long shopGroupId,
            @Valid @RequestBody ShopGroupVO request) {
        shopGroupService.update(currentUser.getId(), shopGroupId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{shopGroupId}")
    public ApiResponse<Void> delete(
            @PathVariable Long shopGroupId) {
        shopGroupService.delete(currentUser.getId(), shopGroupId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{shopGroupId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long shopGroupId) {
        return ApiResponse.ok("조회가 완료되었습니다.", shopGroupService.getUsage(currentUser.getId(), shopGroupId));
    }
}
