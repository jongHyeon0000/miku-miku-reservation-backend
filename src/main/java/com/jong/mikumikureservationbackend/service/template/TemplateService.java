package com.jong.mikumikureservationbackend.service.template;

import com.jong.mikumikureservationbackend.common.constant.TemplateType;
import com.jong.mikumikureservationbackend.vo.template.TemplateImportResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplatePreviewResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateVO;

import java.util.List;

public interface TemplateService {

    /** 템플릿 목록 (종류, 이름 순). templateType이 null이면 전체 */
    List<TemplateVO> getList(TemplateType templateType);

    /** 가져오기 미리보기: 추가될 항목과 이미 있어 제외될 항목. DB는 바꾸지 않는다. 없는 템플릿이면 404 */
    TemplatePreviewResponseVO preview(Long userId, Long templateId);

    /** 템플릿을 통째로 내 분류에 가져온다. 같은 이름은 건너뛰고 나머지를 추가한다. 없는 템플릿이면 404 */
    TemplateImportResponseVO importTemplate(Long userId, Long templateId);
}
