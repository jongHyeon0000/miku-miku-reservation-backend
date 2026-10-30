package com.jong.mikumikureservationbackend.mapper.template;

import com.jong.mikumikureservationbackend.vo.template.TemplateSkippedItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/* 제조사 템플릿을 내 분류(maker)로 가져온다. 제조사는 상위가 없다 */
@Mapper
public interface TemplateMakerMapper {

    int selectAddCount(@Param("userId") Long userId, @Param("templateId") Long templateId);

    List<TemplateSkippedItemVO> selectSkipped(@Param("userId") Long userId, @Param("templateId") Long templateId);

    List<TemplateSkippedItemVO> selectAdded(@Param("userId") Long userId, @Param("templateId") Long templateId);

    long selectMaxItemId(@Param("userId") Long userId);

    int insertItems(@Param("userId") Long userId, @Param("templateId") Long templateId);

    int insertItemThumbnails(@Param("userId") Long userId, @Param("templateId") Long templateId, @Param("maxItemId") long maxItemId);
}
