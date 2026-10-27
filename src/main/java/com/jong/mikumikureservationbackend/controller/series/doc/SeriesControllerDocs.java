package com.jong.mikumikureservationbackend.controller.series.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesDeleteRequestVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* SeriesController의 Swagger 설명 전용 인터페이스. 로직은 SeriesController에만 둔다 */
@Tag(name = "시리즈", description = "시리즈 하위 분류(예: 팝업퍼레이드). 상품은 이 단위로 여러 개 고른다.")
public interface SeriesControllerDocs {

    @Operation(
            summary = "시리즈 목록 조회 (예: 팝업퍼레이드)",
            description = "쿼리: groupId: 상위 시리즈 그룹 id (선택, 생략하면 전체), page: 페이지 번호 (1부터, 기본 1), size: 한 페이지 개수 (기본 10)\n\n"
                    + "응답: items(id, userId, groupId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt), page, size, totalCount, totalPages. 이름이 기타인 항목은 항상 마지막")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "items, page, size, totalCount, totalPages"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "page 또는 size가 1 미만")
    })
    ApiResponse<PageResponseVO<SeriesVO>> getPage(
            @Parameter(description = "상위 시리즈 그룹 id (생략하면 전체)", example = "1") Long groupId,
            @Parameter(description = "페이지 번호 (1부터)", example = "1") int page,
            @Parameter(description = "한 페이지 개수 (기본 10)", example = "10") int size);

    @Operation(
            summary = "시리즈 추가 (예: 팝업퍼레이드)",
            description = "요청 본문: groupId: 상위 시리즈 그룹 id, name: 시리즈 이름 (본문, 둘 다 필수, 이름은 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, userId, groupId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"groupId\":1,\"name\":\"팝업퍼레이드\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, userId, groupId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상위 시리즈 그룹 없음")
    })
    ApiResponse<SeriesVO> create(SeriesVO request);

    @Operation(
            summary = "시리즈 이름과 설명 수정 (예: 팝업퍼레이드)",
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
            @Parameter(description = "수정할 시리즈 id", example = "1") Long seriesId,
            SeriesVO request);

    @Operation(
            summary = "시리즈 삭제, 쓰던 상품은 '기타'로 이동 (예: 팝업퍼레이드)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가, 상위의 마지막 하위 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "삭제할 시리즈 id", example = "1") Long seriesId);

    @Operation(
            summary = "시리즈 일괄 삭제, 전부 성공 또는 전부 실패 (예: 보컬로이드)",
            description = "`seriesIds`를 JSON 본문으로 받는다. 선택한 항목만 가지고 있던 상품에는 '기타'를 연결하고 연결을 지운 뒤 삭제한다(다른 항목이 남은 상품은 그대로). "
                    + "하나라도 없거나 다른 사용자의 시리즈이면 아무것도 삭제하지 않고 404다. `기타`가 하나라도 있으면 400이다. 한 그룹의 하위를 전부 지우려 하면(마지막 하위 보호) 400이다. "
                    + "성공하면 `data` 없이 `code: OK`만 돌려준다. 썸네일 행과 파일도 함께 정리한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "시리즈 3개 삭제", value = "{\"seriesIds\":[1,2,3]}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "seriesIds 누락 또는 빈 배열, 본문을 읽을 수 없음(숫자가 아닌 값, JSON 형식 오류), 기타 포함, 한 그룹의 하위 전체 삭제(마지막 하위 보호)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "하나라도 없는 시리즈(아무것도 삭제하지 않음)")
    })
    ApiResponse<Void> deleteAll(SeriesDeleteRequestVO request);

    @Operation(
            summary = "시리즈 삭제 전 영향 상품 수 조회 (예: 팝업퍼레이드)",
            description = "응답: count: 이 시리즈를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 시리즈를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<UsageResponseVO> getUsage(
            @Parameter(description = "조회할 시리즈 id", example = "1") Long seriesId);
}
