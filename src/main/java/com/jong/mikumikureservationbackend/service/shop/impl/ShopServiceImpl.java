package com.jong.mikumikureservationbackend.service.shop.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.shop.ShopGroupMapper;
import com.jong.mikumikureservationbackend.mapper.shop.ShopMapper;
import com.jong.mikumikureservationbackend.service.shop.ShopService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final ShopGroupMapper shopGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public PageResponseVO<ShopVO> getPage(Long userId, Long groupId, int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "page, size는 1 이상이어야 합니다");
        }

        long offset = (long) (page - 1) * size;
        long totalCount = shopMapper.countAll(userId, groupId);
        List<ShopVO> items = shopMapper.selectPage(userId, groupId, CategoryConstant.DEFAULT_NAME, offset, size);

        return new PageResponseVO<>(items, page, size, totalCount);
    }

    @Override
    @Transactional
    public ShopVO create(Long userId, ShopVO vo) {
        Long shopGroupId = vo.getGroupId();

        if (shopGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "shopGroupId: 필수 값입니다");
        }
        if (shopGroupMapper.selectById(userId, shopGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        String trimmed = vo.getName().trim();

        try {
            shopMapper.insert(userId, shopGroupId, trimmed, vo.getDescription(), vo.getUrl());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }

        return shopMapper.selectByParentAndName(userId, shopGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long shopId, ShopVO vo) {
        ShopVO child = find(userId, shopId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            shopMapper.update(userId, shopId, vo.getName().trim(), vo.getDescription(), vo.getUrl());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long shopId) {
        ShopVO child = find(userId, shopId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (shopMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }

        Long fallbackId = shopMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);

        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        shopMapper.moveUsage(userId, shopId, fallbackId);
        shopMapper.delete(userId, shopId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SHOP, Collections.singletonList(shopId));
    }

    @Override
    @Transactional
    public void deleteAll(Long userId, List<Long> shopIds) {
        List<Long> distinctIds = distinct(shopIds);

        if (distinctIds.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "삭제할 판매처 id가 필요합니다");
        }

        List<ShopVO> items = shopMapper.selectByIds(userId, distinctIds);

        if (items.size() != distinctIds.size()) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        Map<Long, Integer> selectedCounts = new HashMap<>();

        for (ShopVO item : items) {
            if (CategoryConstant.DEFAULT_NAME.equals(item.getName())) {
                throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
            }
            selectedCounts.merge(item.getGroupId(), 1, Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : selectedCounts.entrySet()) {
            if (shopMapper.countByParent(userId, entry.getKey()) - entry.getValue() <= 0) {
                throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
            }
        }

        Long fallbackId = shopMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);

        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        shopMapper.moveUsageByIds(userId, distinctIds, fallbackId);
        shopMapper.deleteByIds(userId, distinctIds);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SHOP, distinctIds);
    }

    @Override
    public UsageResponseVO getUsage(Long userId, Long shopId) {
        find(userId, shopId);

        return new UsageResponseVO(shopMapper.countUsage(userId, shopId));
    }

    private List<Long> distinct(List<Long> shopIds) {
        if (shopIds == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(new LinkedHashSet<>(shopIds));
    }

    private ShopVO find(Long userId, Long shopId) {
        ShopVO child = shopMapper.selectById(userId, shopId);

        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        return child;
    }
}
