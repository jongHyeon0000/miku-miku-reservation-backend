package com.jong.mikumikureservationbackend.service.series.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.series.SeriesGroupMapper;
import com.jong.mikumikureservationbackend.mapper.series.SeriesMapper;
import com.jong.mikumikureservationbackend.service.series.SeriesService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeriesServiceImpl implements SeriesService {

    private final SeriesMapper seriesMapper;
    private final SeriesGroupMapper seriesGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public PageResponseVO<SeriesVO> getPage(Long userId, Long groupId, int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "page, size는 1 이상이어야 합니다");
        }

        long offset = (long) (page - 1) * size;
        long totalCount = seriesMapper.countAll(userId, groupId);
        List<SeriesVO> items = seriesMapper.selectPage(userId, groupId, CategoryConstant.DEFAULT_NAME, offset, size);

        return new PageResponseVO<>(items, page, size, totalCount);
    }

    @Override
    @Transactional
    public SeriesVO create(Long userId, SeriesVO vo) {
        Long seriesGroupId = vo.getGroupId();

        if (seriesGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "seriesGroupId: 필수 값입니다");
        }
        if (seriesGroupMapper.selectById(userId, seriesGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        String trimmed = vo.getName().trim();

        try {
            seriesMapper.insert(userId, seriesGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }

        return seriesMapper.selectByParentAndName(userId, seriesGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long seriesId, SeriesVO vo) {
        SeriesVO child = find(userId, seriesId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            seriesMapper.update(userId, seriesId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long seriesId) {
        SeriesVO child = find(userId, seriesId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (seriesMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }

        Long fallbackId = seriesMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);

        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        seriesMapper.moveInsertFallback(userId, seriesId, fallbackId);
        seriesMapper.moveDeleteLinks(userId, seriesId);
        seriesMapper.delete(userId, seriesId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.SERIES, Collections.singletonList(seriesId));
    }

    @Override
    public UsageResponseVO getUsage(Long userId, Long seriesId) {
        find(userId, seriesId);

        return new UsageResponseVO(seriesMapper.countUsage(userId, seriesId));
    }

    private SeriesVO find(Long userId, Long seriesId) {
        SeriesVO child = seriesMapper.selectById(userId, seriesId);

        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        return child;
    }
}
