package com.jong.mikumikureservationbackend.controller.shop.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.shop.ShopVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* ShopController의 Swagger 설명 전용 인터페이스. 로직은 ShopController에만 둔다 */
@Tag(name = "판매처 스토어", description = "판매처 하위 분류(예: 굿스마일 pw). 상품은 이 단위로 고른다.")
public interface ShopControllerDocs {

    @Operation(
            summary = "판매처 스토어 목록 조회 (예: 굿스마일 pw)",
            description = "쿼리: groupId: 상위 판매처 채널 id (선택, 생략하면 전체), page: 페이지 번호 (1부터, 기본 1), size: 한 페이지 개수 (기본 10)\n\n"
                    + "응답: items(id, userId, groupId, name, description, url(없으면 생략), thumbnailUrl(없으면 생략), createdAt, updatedAt), page, size, totalCount, totalPages. 이름이 기타인 항목은 항상 마지막")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "items, page, size, totalCount, totalPages"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "page 또는 size가 1 미만")
    })
    ApiResponse<PageResponseVO<ShopVO>> getPage(
            @Parameter(description = "상위 판매처 채널 id (생략하면 전체)", example = "1") Long groupId,
            @Parameter(description = "페이지 번호 (1부터)", example = "1") int page,
            @Parameter(description = "한 페이지 개수 (기본 10)", example = "10") int size);

    @Operation(
            summary = "판매처 스토어 추가 (예: 굿스마일 pw)",
            description = "요청 본문: groupId: 상위 판매처 채널 id, name: 판매처 스토어 이름 (본문, 둘 다 필수, 이름은 100자 이하), description: 설명 (본문, 선택, 1000자 이하), url: 판매처 바로가기 주소 (본문, 선택, 2000자 이하)\n\n"
                    + "응답: 생성된 id, userId, groupId, name, description, url(없으면 생략), thumbnailUrl(없으면 생략), createdAt, updatedAt")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"groupId\":1,\"name\":\"굿스마일 pw\",\"description\":\"설명(선택)\",\"url\":\"https://www.goodsmile.com\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, userId, groupId, name, description, url(없으면 생략), thumbnailUrl(없으면 생략), createdAt, updatedAt"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상위 판매처 채널 없음")
    })
    ApiResponse<ShopVO> create(ShopVO request);

    @Operation(
            summary = "판매처 스토어 이름, 설명, 주소 수정 (예: 굿스마일 pw)",
            description = "요청 본문: name: 새 이름 (본문, 필수, 100자 이하), description: 새 설명 (본문, 선택, 1000자 이하, 생략하면 설명 삭제), url: 새 주소 (본문, 선택, 2000자 이하, 생략하면 주소 삭제)\n\n"
                    + "응답: 성공 시 data 없음")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "수정 예",
                    value = "{\"name\":\"새 이름\",\"description\":\"새 설명\",\"url\":\"https://www.goodsmile.com\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 이름 변경 불가, 이름 중복, 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> update(
            @Parameter(description = "수정할 판매처 스토어 id", example = "1") Long shopId,
            ShopVO request);

    @Operation(
            summary = "판매처 스토어 삭제, 쓰던 상품은 '기타'로 이동 (예: 굿스마일 pw)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가, 상위의 마지막 하위 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "삭제할 판매처 스토어 id", example = "1") Long shopId);

    @Operation(
            summary = "판매처 스토어 삭제 전 영향 상품 수 조회 (예: 굿스마일 pw)",
            description = "응답: count: 이 판매처 스토어를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 판매처 스토어를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<UsageResponseVO> getUsage(
            @Parameter(description = "조회할 판매처 스토어 id", example = "1") Long shopId);
}
