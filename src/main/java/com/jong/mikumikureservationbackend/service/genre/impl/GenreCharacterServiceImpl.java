package com.jong.mikumikureservationbackend.service.genre.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.mikumikureservationbackend.mapper.genre.GenreCharacterMapper;
import com.jong.mikumikureservationbackend.service.genre.GenreCharacterService;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;
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
public class GenreCharacterServiceImpl implements GenreCharacterService {

    private final GenreCharacterMapper genreCharacterMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final ThumbnailImageService thumbnailImageService;

    @Override
    public PageResponseVO<GenreCharacterVO> getPage(Long userId, Long groupId, int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "page, size는 1 이상이어야 합니다");
        }

        long offset = (long) (page - 1) * size;
        long totalCount = genreCharacterMapper.countAll(userId, groupId);
        List<GenreCharacterVO> items = genreCharacterMapper.selectPage(userId, groupId, CategoryConstant.DEFAULT_NAME, offset, size);

        return new PageResponseVO<>(items, page, size, totalCount);
    }

    @Override
    @Transactional
    public GenreCharacterVO create(Long userId, GenreCharacterVO vo) {
        Long genreCharacterGroupId = vo.getGroupId();

        if (genreCharacterGroupId == null) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "genreCharacterGroupId: 필수 값입니다");
        }
        if (genreCharacterGroupMapper.selectById(userId, genreCharacterGroupId) == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        String trimmed = vo.getName().trim();

        try {
            genreCharacterMapper.insert(userId, genreCharacterGroupId, trimmed, vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }

        return genreCharacterMapper.selectByParentAndName(userId, genreCharacterGroupId, trimmed);
    }

    @Override
    @Transactional
    public void update(Long userId, Long genreCharacterId, GenreCharacterVO vo) {
        GenreCharacterVO child = find(userId, genreCharacterId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())
                && !CategoryConstant.DEFAULT_NAME.equals(vo.getName().trim())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        try {
            genreCharacterMapper.update(userId, genreCharacterId, vo.getName().trim(), vo.getDescription());
        } catch (DuplicateKeyException e) {
            throw new BusinessException(GlobalExceptionCode.DUPLICATE_NAME);
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long genreCharacterId) {
        GenreCharacterVO child = find(userId, genreCharacterId);

        if (CategoryConstant.DEFAULT_NAME.equals(child.getName())) {
            throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
        }
        if (genreCharacterMapper.countByParent(userId, child.getGroupId()) <= 1) {
            throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
        }

        Long fallbackId = genreCharacterMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);

        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        genreCharacterMapper.moveInsertFallback(userId, genreCharacterId, fallbackId);
        genreCharacterMapper.moveDeleteLinks(userId, genreCharacterId);
        genreCharacterMapper.delete(userId, genreCharacterId);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.GENRE_CHARACTER, Collections.singletonList(genreCharacterId));
    }

    @Override
    @Transactional
    public void deleteAll(Long userId, List<Long> genreCharacterIds) {
        List<Long> distinctIds = distinct(genreCharacterIds);

        if (distinctIds.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "삭제할 캐릭터 id가 필요합니다");
        }

        List<GenreCharacterVO> items = genreCharacterMapper.selectByIds(userId, distinctIds);

        if (items.size() != distinctIds.size()) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        Map<Long, Integer> selectedCounts = new HashMap<>();

        for (GenreCharacterVO item : items) {
            if (CategoryConstant.DEFAULT_NAME.equals(item.getName())) {
                throw new BusinessException(GlobalExceptionCode.PROTECTED_CATEGORY);
            }
            selectedCounts.merge(item.getGroupId(), 1, Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : selectedCounts.entrySet()) {
            if (genreCharacterMapper.countByParent(userId, entry.getKey()) - entry.getValue() <= 0) {
                throw new BusinessException(GlobalExceptionCode.LAST_CHILD_CATEGORY);
            }
        }

        Long fallbackId = genreCharacterMapper.selectDefaultId(userId, CategoryConstant.DEFAULT_NAME);

        if (fallbackId == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        genreCharacterMapper.moveInsertFallbackByIds(userId, distinctIds, fallbackId);
        genreCharacterMapper.moveDeleteLinksByIds(userId, distinctIds);
        genreCharacterMapper.deleteByIds(userId, distinctIds);
        thumbnailImageService.deleteByTargets(userId, ThumbnailTarget.GENRE_CHARACTER, distinctIds);
    }

    @Override
    public UsageResponseVO getUsage(Long userId, Long genreCharacterId) {
        find(userId, genreCharacterId);

        return new UsageResponseVO(genreCharacterMapper.countUsage(userId, genreCharacterId));
    }

    private List<Long> distinct(List<Long> genreCharacterIds) {
        if (genreCharacterIds == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(new LinkedHashSet<>(genreCharacterIds));
    }

    private GenreCharacterVO find(Long userId, Long genreCharacterId) {
        GenreCharacterVO child = genreCharacterMapper.selectById(userId, genreCharacterId);

        if (child == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        return child;
    }
}
