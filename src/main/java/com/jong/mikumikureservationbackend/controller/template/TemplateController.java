package com.jong.mikumikureservationbackend.controller.template;

import com.jong.mikumikureservationbackend.common.constant.TemplateType;
import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.template.doc.TemplateControllerDocs;
import com.jong.mikumikureservationbackend.service.template.TemplateService;
import com.jong.mikumikureservationbackend.vo.template.TemplateImportResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplatePreviewResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController implements TemplateControllerDocs {

    private final TemplateService templateService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<List<TemplateVO>> getList(
            @RequestParam(required = false) TemplateType templateType) {
        return ApiResponse.ok("조회가 완료되었습니다.", templateService.getList(templateType));
    }

    @Override
    @GetMapping("/{templateId}/import-preview")
    public ApiResponse<TemplatePreviewResponseVO> preview(
            @PathVariable Long templateId) {
        return ApiResponse.ok("조회가 완료되었습니다.", templateService.preview(currentUser.getId(), templateId));
    }

    @Override
    @PostMapping("/{templateId}/import")
    public ApiResponse<TemplateImportResponseVO> importTemplate(
            @PathVariable Long templateId) {
        return ApiResponse.ok("가져오기가 완료되었습니다.", templateService.importTemplate(currentUser.getId(), templateId));
    }
}
