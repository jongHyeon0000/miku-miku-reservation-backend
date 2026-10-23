package com.jong.mikumikureservationbackend.controller.template;

import com.jong.mikumikureservationbackend.common.constant.TemplateType;
import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.service.template.TemplateService;
import com.jong.mikumikureservationbackend.vo.template.TemplateImportResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplatePreviewResponseVO;
import com.jong.mikumikureservationbackend.vo.template.TemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "템플릿", description = "미리 만들어 둔 분류 묶음(예: 동방프로젝트)을 내 분류로 가져온다. 가져온 분류는 직접 만든 것과 똑같이 수정, 삭제할 수 있다.")
@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "템플릿 목록 조회 (예: 동방프로젝트)",
            description = "정렬: 종류 → 이름 순.\n\n"
                    + "응답: templateId, templateType, name, description, itemCount(가져오면 만들어지는 하위 항목 수, 상위 그룹 제외), thumbnailUrl(대표 이미지)\n\n"
                    + "templateType: SHOP 판매처, FIGURE_TYPE 형태, SERIES 시리즈, GENRE_CHARACTER 장르/캐릭터, MAKER 제조사")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "템플릿 목록 (없으면 빈 배열)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "templateType이 정해진 값이 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료")
    })
    @GetMapping
    public ApiResponse<List<TemplateVO>> getList(
            @Parameter(description = "템플릿 종류 (생략하면 전체)", example = "GENRE_CHARACTER")
            @RequestParam(required = false) TemplateType templateType) {
        return ApiResponse.ok("조회가 완료되었습니다.", templateService.getList(templateType));
    }

    @Operation(
            summary = "템플릿 가져오기 미리보기, DB 변경 없음 (예: 동방프로젝트)",
            description = "가져오기 전에 확인창을 띄우려고 쓴다.\n\n"
                    + "응답: templateId, name, addCount(새로 추가될 하위 항목 수), skippedCount(제외될 수), "
                    + "skipped(제외될 항목을 상위 그룹별로 묶음: groupName, names. 제조사는 상위가 없어 groupName 생략, 없으면 빈 배열)\n\n"
                    + "제외 기준: 내 분류에서 같은 상위 안에 같은 이름이 이미 있는 항목 (등록할 때 '이미 같은 이름이 있습니다'로 막히는 기준과 같다)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "addCount, skippedCount, skipped"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 템플릿")
    })
    @GetMapping("/{templateId}/import-preview")
    public ApiResponse<TemplatePreviewResponseVO> preview(
            @Parameter(description = "템플릿 id", example = "1") @PathVariable Long templateId) {
        return ApiResponse.ok("조회가 완료되었습니다.", templateService.preview(currentUser.getId(), templateId));
    }

    @Operation(
            summary = "템플릿 가져오기 (예: 동방프로젝트)",
            description = "요청 본문 없음. 템플릿을 통째로 내 분류에 복사한다. 전부 성공하거나 전부 취소된다.\n\n"
                    + "상위 그룹: 같은 이름이 있으면 합치고, 없으면 새로 만들며 기본 '기타' 하위도 함께 만든다.\n\n"
                    + "하위 항목: 같은 이름이 있으면 건너뛰고 없으면 추가한다. 판매처는 바로가기 주소(url)도 함께 복사한다.\n\n"
                    + "이미지: 템플릿의 공용 이미지를 새로 추가된 상위, 하위에만 연결한다(파일 복사 없음). 이미 있던 항목은 건드리지 않는다.\n\n"
                    + "응답: addedCount(추가된 하위 항목 수), skippedCount(건너뛴 수)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "addedCount, skippedCount (모두 이미 있으면 addedCount 0, 그래도 200)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음, 만료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 템플릿")
    })
    @PostMapping("/{templateId}/import")
    public ApiResponse<TemplateImportResponseVO> importTemplate(
            @Parameter(description = "템플릿 id", example = "1") @PathVariable Long templateId) {
        return ApiResponse.ok("가져오기가 완료되었습니다.", templateService.importTemplate(currentUser.getId(), templateId));
    }
}
