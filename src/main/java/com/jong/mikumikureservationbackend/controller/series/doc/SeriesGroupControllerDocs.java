package com.jong.mikumikureservationbackend.controller.series.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.series.SeriesGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

/* SeriesGroupController의 Swagger 설명 전용 인터페이스. 로직은 SeriesGroupController에만 둔다 */
@Tag(name = "시리즈 그룹", description = "시리즈 상위 분류(예: 굿스마일 라인업). 추가하면 기본 하위 '기타'가 함께 만들어진다.")
public interface SeriesGroupControllerDocs {

    @Operation(
            summary = "시리즈 그룹 목록 조회, 하위 시리즈 중첩 (예: 굿스마일 라인업)",
            description = "응답: id, name, description, thumbnailUrl, children(시리즈 목록: id, groupId, name, description, thumbnailUrl)")
    ApiResponse<List<SeriesGroupVO>> getTree();

    @Operation(
            summary = "시리즈 그룹 추가, 기본 하위 '기타' 자동 생성 (예: 굿스마일 라인업)",
            description = "요청 본문: name: 시리즈 그룹 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, name, description, thumbnailUrl, children(기본 '기타' 하나)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"name\":\"굿스마일 라인업\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, name, description, thumbnailUrl, children(기본 '기타' 하나)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류")
    })
    ApiResponse<SeriesGroupVO> create(SeriesGroupVO request);

    @Operation(
            summary = "시리즈 그룹 이름과 설명 수정 (예: 굿스마일 라인업)",
            description = "요청 본문: name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제)\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "수정 예",
                    value = "{\"name\":\"새 이름\",\"description\":\"새 설명(생략하면 설명 삭제)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 이름 변경 불가, 이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> update(
            @Parameter(description = "수정할 시리즈 그룹 id", example = "1") Long seriesGroupId,
            SeriesGroupVO request);

    @Operation(
            summary = "시리즈 그룹 삭제, 하위와 쓰던 상품은 '기타'로 이동 (예: 굿스마일 라인업)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "삭제할 시리즈 그룹 id", example = "1") Long seriesGroupId);

    @Operation(
            summary = "시리즈 그룹 삭제 전 영향 상품 수 조회 (예: 굿스마일 라인업)",
            description = "응답: count: 이 시리즈 그룹의 하위를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 시리즈 그룹의 하위를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<UsageResponseVO> getUsage(
            @Parameter(description = "조회할 시리즈 그룹 id", example = "1") Long seriesGroupId);
}
