package com.jong.mikumikureservationbackend.controller.type.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.type.FigureTypeGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

/* FigureTypeGroupController의 Swagger 설명 전용 인터페이스. 로직은 FigureTypeGroupController에만 둔다 */
@Tag(name = "형태", description = "형태 상위 분류(예: 스케일, 넨도로이드). 추가하면 기본 하위 '기타'가 함께 만들어진다.")
public interface FigureTypeGroupControllerDocs {

    @Operation(
            summary = "형태 목록 조회, 하위 형태 하위 중첩 (예: 스케일, 넨도로이드)",
            description = "응답: id, userId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt, children(형태 하위 목록: id, userId, groupId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt)")
    ApiResponse<List<FigureTypeGroupVO>> getTree();

    @Operation(
            summary = "형태 추가, 기본 하위 '기타' 자동 생성 (예: 스케일, 넨도로이드)",
            description = "요청 본문: name: 형태 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, userId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt, children(기본 '기타' 하나)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"name\":\"스케일\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, userId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt, children(기본 '기타' 하나)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류")
    })
    ApiResponse<FigureTypeGroupVO> create(FigureTypeGroupVO request);

    @Operation(
            summary = "형태 이름과 설명 수정 (예: 스케일, 넨도로이드)",
            description = "요청 본문: name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제)\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "수정 예",
                    value = "{\"name\":\"새 이름\",\"description\":\"새 설명\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 이름 변경 불가, 이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> update(
            @Parameter(description = "수정할 형태 id", example = "1") Long figureTypeGroupId,
            FigureTypeGroupVO request);

    @Operation(
            summary = "형태 삭제, 하위도 함께 삭제되고 쓰던 상품은 '기타'로 이동 (예: 스케일, 넨도로이드)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "삭제할 형태 id", example = "1") Long figureTypeGroupId);

    @Operation(
            summary = "형태 삭제 전 영향 상품 수 조회 (예: 스케일, 넨도로이드)",
            description = "응답: count: 이 형태의 하위를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 형태의 하위를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<UsageResponseVO> getUsage(
            @Parameter(description = "조회할 형태 id", example = "1") Long figureTypeGroupId);
}
