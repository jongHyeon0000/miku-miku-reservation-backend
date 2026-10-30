package com.jong.mikumikureservationbackend.service.template.impl;

import com.jong.mikumikureservationbackend.common.constant.CategoryConstant;
import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.TemplateType;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.template.TemplateFigureTypeMapper;
import com.jong.mikumikureservationbackend.mapper.template.TemplateGenreCharacterMapper;
import com.jong.mikumikureservationbackend.mapper.template.TemplateMakerMapper;
import com.jong.mikumikureservationbackend.mapper.template.TemplateMapper;
import com.jong.mikumikureservationbackend.mapper.template.TemplateSeriesMapper;
import com.jong.mikumikureservationbackend.mapper.template.TemplateShopMapper;
import com.jong.mikumikureservationbackend.service.template.TemplateService;
import com.jong.mikumikureservationbackend.vo.template.TemplateImportResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplatePreviewResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateSkippedGroupVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateSkippedItemResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateSkippedItemVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final TemplateMapper templateMapper;
    private final TemplateShopMapper templateShopMapper;
    private final TemplateFigureTypeMapper templateFigureTypeMapper;
    private final TemplateSeriesMapper templateSeriesMapper;
    private final TemplateGenreCharacterMapper templateGenreCharacterMapper;
    private final TemplateMakerMapper templateMakerMapper;

    @Override
    public List<TemplateVO> getList(TemplateType templateType) {
        return templateMapper.selectAll(templateType);
    }

    @Override
    public TemplatePreviewResponseVO preview(Long userId, Long templateId) {
        TemplateVO template = find(templateId);
        int addCount;
        List<TemplateSkippedItemVO> added;
        List<TemplateSkippedItemVO> skipped;

        switch (template.getTemplateType()) {
            case SHOP:
                addCount = templateShopMapper.selectAddCount(userId, templateId);
                added = templateShopMapper.selectAdded(userId, templateId);
                skipped = templateShopMapper.selectSkipped(userId, templateId);
                break;
            case FIGURE_TYPE:
                addCount = templateFigureTypeMapper.selectAddCount(userId, templateId);
                added = templateFigureTypeMapper.selectAdded(userId, templateId);
                skipped = templateFigureTypeMapper.selectSkipped(userId, templateId);
                break;
            case SERIES:
                addCount = templateSeriesMapper.selectAddCount(userId, templateId);
                added = templateSeriesMapper.selectAdded(userId, templateId);
                skipped = templateSeriesMapper.selectSkipped(userId, templateId);
                break;
            case GENRE_CHARACTER:
                addCount = templateGenreCharacterMapper.selectAddCount(userId, templateId);
                added = templateGenreCharacterMapper.selectAdded(userId, templateId);
                skipped = templateGenreCharacterMapper.selectSkipped(userId, templateId);
                break;
            case MAKER:
                addCount = templateMakerMapper.selectAddCount(userId, templateId);
                added = templateMakerMapper.selectAdded(userId, templateId);
                skipped = templateMakerMapper.selectSkipped(userId, templateId);
                break;
            default:
                throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }

        return new TemplatePreviewResponseVO(templateId, template.getName(), addCount, skipped.size(), groupItems(added), groupItems(skipped));
    }

    @Override
    @Transactional
    public TemplateImportResponseVO importTemplate(Long userId, Long templateId) {
        TemplateVO template = find(templateId);

        switch (template.getTemplateType()) {
            case SHOP:
                return importShop(userId, templateId);
            case FIGURE_TYPE:
                return importFigureType(userId, templateId);
            case SERIES:
                return importSeries(userId, templateId);
            case GENRE_CHARACTER:
                return importGenreCharacter(userId, templateId);
            case MAKER:
                return importMaker(userId, templateId);
            default:
                throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
    }

    /* 상위가 있는 종류는 순서가 같다: 없는 상위 생성 → 새 상위의 기본 하위(기타) → 새 상위 이미지 → 제외 목록 → 없는 하위 추가 → 새 하위 이미지.
       이미지는 기준 id보다 큰(이번에 만든) 행에만 연결한다 */
    private TemplateImportResponseVO importShop(Long userId, Long templateId) {
        long maxGroupId = templateShopMapper.selectMaxGroupId(userId);

        templateShopMapper.insertGroups(userId, templateId);
        templateShopMapper.insertDefaultItems(userId, templateId, CategoryConstant.DEFAULT_NAME);
        templateShopMapper.insertGroupThumbnails(userId, templateId, maxGroupId);

        int skippedCount = templateShopMapper.selectSkipped(userId, templateId).size();
        long maxItemId = templateShopMapper.selectMaxItemId(userId);
        int addedCount = templateShopMapper.insertItems(userId, templateId);

        templateShopMapper.insertItemThumbnails(userId, templateId, maxItemId);

        return new TemplateImportResponseVO(addedCount, skippedCount);
    }

    private TemplateImportResponseVO importFigureType(Long userId, Long templateId) {
        long maxGroupId = templateFigureTypeMapper.selectMaxGroupId(userId);

        templateFigureTypeMapper.insertGroups(userId, templateId);
        templateFigureTypeMapper.insertDefaultItems(userId, templateId, CategoryConstant.DEFAULT_NAME);
        templateFigureTypeMapper.insertGroupThumbnails(userId, templateId, maxGroupId);

        int skippedCount = templateFigureTypeMapper.selectSkipped(userId, templateId).size();
        long maxItemId = templateFigureTypeMapper.selectMaxItemId(userId);
        int addedCount = templateFigureTypeMapper.insertItems(userId, templateId);

        templateFigureTypeMapper.insertItemThumbnails(userId, templateId, maxItemId);

        return new TemplateImportResponseVO(addedCount, skippedCount);
    }

    private TemplateImportResponseVO importSeries(Long userId, Long templateId) {
        long maxGroupId = templateSeriesMapper.selectMaxGroupId(userId);

        templateSeriesMapper.insertGroups(userId, templateId);
        templateSeriesMapper.insertDefaultItems(userId, templateId, CategoryConstant.DEFAULT_NAME);
        templateSeriesMapper.insertGroupThumbnails(userId, templateId, maxGroupId);

        int skippedCount = templateSeriesMapper.selectSkipped(userId, templateId).size();
        long maxItemId = templateSeriesMapper.selectMaxItemId(userId);
        int addedCount = templateSeriesMapper.insertItems(userId, templateId);

        templateSeriesMapper.insertItemThumbnails(userId, templateId, maxItemId);

        return new TemplateImportResponseVO(addedCount, skippedCount);
    }

    private TemplateImportResponseVO importGenreCharacter(Long userId, Long templateId) {
        long maxGroupId = templateGenreCharacterMapper.selectMaxGroupId(userId);

        templateGenreCharacterMapper.insertGroups(userId, templateId);
        templateGenreCharacterMapper.insertDefaultItems(userId, templateId, CategoryConstant.DEFAULT_NAME);
        templateGenreCharacterMapper.insertGroupThumbnails(userId, templateId, maxGroupId);

        int skippedCount = templateGenreCharacterMapper.selectSkipped(userId, templateId).size();
        long maxItemId = templateGenreCharacterMapper.selectMaxItemId(userId);
        int addedCount = templateGenreCharacterMapper.insertItems(userId, templateId);

        templateGenreCharacterMapper.insertItemThumbnails(userId, templateId, maxItemId);

        return new TemplateImportResponseVO(addedCount, skippedCount);
    }

    private TemplateImportResponseVO importMaker(Long userId, Long templateId) {
        int skippedCount = templateMakerMapper.selectSkipped(userId, templateId).size();
        long maxItemId = templateMakerMapper.selectMaxItemId(userId);
        int addedCount = templateMakerMapper.insertItems(userId, templateId);

        templateMakerMapper.insertItemThumbnails(userId, templateId, maxItemId);

        return new TemplateImportResponseVO(addedCount, skippedCount);
    }

    private TemplateVO find(Long templateId) {
        TemplateVO template = templateMapper.selectById(templateId);

        if (template == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        return template;
    }

    /* 추가, 제외 항목을 상위 그룹 이름별로 묶는다 (조회 순서 유지, 제조사는 상위가 없어 groupName이 null 하나로 묶인다) */
    private List<TemplateSkippedGroupVO> groupItems(List<TemplateSkippedItemVO> rows) {
        Map<String, List<TemplateSkippedItemResponseVO>> grouped = new LinkedHashMap<>();

        for (TemplateSkippedItemVO row : rows) {
            List<TemplateSkippedItemResponseVO> items = grouped.get(row.getGroupName());

            if (items == null) {
                items = new ArrayList<>();
                grouped.put(row.getGroupName(), items);
            }

            items.add(new TemplateSkippedItemResponseVO(row.getName(), row.getDescription(), row.getThumbnailUrl()));
        }

        List<TemplateSkippedGroupVO> result = new ArrayList<>();

        for (Map.Entry<String, List<TemplateSkippedItemResponseVO>> entry : grouped.entrySet()) {
            result.add(new TemplateSkippedGroupVO(entry.getKey(), entry.getValue()));
        }

        return result;
    }
}
