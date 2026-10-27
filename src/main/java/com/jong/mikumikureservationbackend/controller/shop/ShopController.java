package com.jong.mikumikureservationbackend.controller.shop;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.shop.doc.ShopControllerDocs;
import com.jong.mikumikureservationbackend.service.shop.ShopService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopDeleteRequestVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopVO;
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
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController implements ShopControllerDocs {

    private final ShopService shopService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<PageResponseVO<ShopVO>> getPage(
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", shopService.getPage(currentUser.getId(), groupId, page, size));
    }

    @Override
    @PostMapping
    public ApiResponse<ShopVO> create(@Valid @RequestBody ShopVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", shopService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{shopId}")
    public ApiResponse<Void> update(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopVO request) {
        shopService.update(currentUser.getId(), shopId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{shopId}")
    public ApiResponse<Void> delete(
            @PathVariable Long shopId) {
        shopService.delete(currentUser.getId(), shopId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @DeleteMapping
    public ApiResponse<Void> deleteAll(@RequestBody ShopDeleteRequestVO request) {
        shopService.deleteAll(currentUser.getId(), request.getShopIds());

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{shopId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long shopId) {
        return ApiResponse.ok("조회가 완료되었습니다.", shopService.getUsage(currentUser.getId(), shopId));
    }
}
