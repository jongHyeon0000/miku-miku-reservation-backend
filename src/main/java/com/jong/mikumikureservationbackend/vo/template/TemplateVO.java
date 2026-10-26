package com.jong.mikumikureservationbackend.vo.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jong.mikumikureservationbackend.common.constant.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 분류 템플릿 (테이블 template)
 * 목록 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "분류 템플릿. 가져오면 시리즈, 캐릭터 등 분류가 내 계정에 복사된다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateVO {

    /** PK */
    @Schema(description = "템플릿 id (미리보기, 가져오기에 사용)", example = "1")
    private Long templateId;

    /** 템플릿 종류: SHOP 판매처, FIGURE_TYPE 형태, SERIES 시리즈, GENRE_CHARACTER 장르/캐릭터, MAKER 제조사 */
    @Schema(description = "템플릿 종류: SHOP 판매처, FIGURE_TYPE 형태, SERIES 시리즈, GENRE_CHARACTER 장르/캐릭터, MAKER 제조사", example = "GENRE_CHARACTER")
    private TemplateType templateType;

    /** 템플릿 이름 (동방프로젝트, 기본 제조사 등) */
    @Schema(description = "템플릿 이름", example = "동방프로젝트")
    private String name;

    /** 설명 (일반 텍스트) */
    @Schema(description = "설명 (일반 텍스트, 없으면 응답에서 빠진다)", example = "동방프로젝트 주요 캐릭터")
    private String description;

    /** [response] 가져오면 만들어지는 하위 항목 수 (상위 그룹은 세지 않는다) */
    @Schema(description = "가져오면 만들어지는 하위 항목 수 (캐릭터, 시리즈 등. 상위 그룹은 제외)", example = "297")
    private Integer itemCount;

    /** [thumbnail_image joined] 대표 이미지 주소 */
    @Schema(description = "대표 이미지 주소 (없으면 응답에서 빠진다)", example = "/uploads/template/thumbnail-image/template/2026/10/abc.png")
    private String thumbnailUrl;
}
