package com.jong.mikumikureservationbackend.mapper.template;

import com.jong.mikumikureservationbackend.common.constant.TemplateType;
import com.jong.mikumikureservationbackend.vo.template.TemplateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateMapper {

    List<TemplateVO> selectAll(@Param("templateType") TemplateType templateType);

    TemplateVO selectById(@Param("templateId") Long templateId);
}
