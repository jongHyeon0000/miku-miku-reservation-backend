package com.jong.mikumikureservationbackend.service.type.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.mikumikureservationbackend.mapper.type.FigureTypeMapper;
import com.jong.mikumikureservationbackend.service.type.FigureTypeService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureTypeServiceImpl implements FigureTypeService {

    private final FigureTypeMapper figureTypeMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public PageResponseVO<FigureTypeVO> getPage(Long userId, Long groupId, int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "page, size는 1 이상이어야 합니다");
        }

        long offset = (long) (page - 1) * size;
        long totalCount = figureTypeMapper.countAll(userId, groupId);
        List<FigureTypeVO> items = figureTypeMapper.selectPage(userId, groupId, CategoryConstant.DEFAULT_NAME, offset, size);

        return new PageResponseVO<>(items, page, size, totalCount);
    }

    @Override
    @Transactional
    public FigureTypeVO create(Long userId, FigureTypeVO vo) {
        Long figureTypeGroupId = vo.getGroupId();

        if (figureTypeGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "figureTypeGroupId: 필수 값입니다");
        }
        if (figureTypeGroupMapper.selectById(userId, figureTypeGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        String trimmed = vo.getName().trim();

        try {
            figureTypeMapper.insert(userId, figureTypeGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }

        return figureTypeMapper.selectByParentAndName(userId, figureTypeGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long figureTypeId, FigureTypeVO vo) {
        FigureTypeVO child = find(userId, figureTypeId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            figureTypeMapper.update(userId, figureTypeId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long figureTypeId) {
        FigureTypeVO child = find(userId, figureTypeId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (figureTypeMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }

        Long fallbackId = figureTypeMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);

        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        figureTypeMapper.moveUsage(userId, figureTypeId, fallbackId);
        figureTypeMapper.delete(userId, figureTypeId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.FIGURE_TYPE, Collections.singletonList(figureTypeId));
    }

    @Override
    public UsageResponseVO getUsage(Long userId, Long figureTypeId) {
        find(userId, figureTypeId);

        return new UsageResponseVO(figureTypeMapper.countUsage(userId, figureTypeId));
    }

    private FigureTypeVO find(Long userId, Long figureTypeId) {
        FigureTypeVO child = figureTypeMapper.selectById(userId, figureTypeId);

        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        return child;
    }
}
