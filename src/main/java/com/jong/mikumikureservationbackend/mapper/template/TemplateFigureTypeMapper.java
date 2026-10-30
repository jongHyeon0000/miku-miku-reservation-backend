package com.jong.mikumikureservationbackend.mapper.template;

import com.jong.mikumikureservationbackend.vo.template.TemplateSkippedItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/* 형태 템플릿을 내 분류(figure_type_group, figure_type)로 가져온다 */
@Mapper
public interface TemplateFigureTypeMapper {

    int selectAddCount(@Param("userId") Long userId, @Param("templateId") Long templateId);

    List<TemplateSkippedItemVO> selectSkipped(@Param("userId") Long userId, @Param("templateId") Long templateId);

    List<TemplateSkippedItemVO> selectAdded(@Param("userId") Long userId, @Param("templateId") Long templateId);

    long selectMaxGroupId(@Param("userId") Long userId);

    long selectMaxItemId(@Param("userId") Long userId);

    int insertGroups(@Param("userId") Long userId, @Param("templateId") Long templateId);

    int insertDefaultItems(@Param("userId") Long userId, @Param("templateId") Long templateId, @Param("defaultName") String defaultName);

    int insertItems(@Param("userId") Long userId, @Param("templateId") Long templateId);

    int insertGroupThumbnails(@Param("userId") Long userId, @Param("templateId") Long templateId, @Param("maxGroupId") long maxGroupId);

    int insertItemThumbnails(@Param("userId") Long userId, @Param("templateId") Long templateId, @Param("maxItemId") long maxItemId);
}
