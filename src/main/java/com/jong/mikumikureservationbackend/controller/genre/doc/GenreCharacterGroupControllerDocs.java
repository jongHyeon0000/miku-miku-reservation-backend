package com.jong.mikumikureservationbackend.controller.genre.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

/* GenreCharacterGroupController의 Swagger 설명 전용 인터페이스. 로직은 GenreCharacterGroupController에만 둔다 */
@Tag(name = "장르", description = "캐릭터 상위 분류인 장르(예: 보컬로이드). 추가하면 기본 하위 '기타'가 함께 만들어진다.")
public interface GenreCharacterGroupControllerDocs {

    @Operation(
            summary = "장르 목록 조회, 하위 캐릭터 중첩 (예: 보컬로이드)",
            description = "응답: id, userId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt, children(캐릭터 목록: id, userId, groupId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt)")
    ApiResponse<List<GenreCharacterGroupVO>> getTree();

    @Operation(
            summary = "장르 추가, 기본 하위 '기타' 자동 생성 (예: 보컬로이드)",
            description = "요청 본문: name: 장르 이름 (본문, 필수, 100자 이하), description: 설명 (본문, 선택, 1000자 이하)\n\n"
                    + "응답: 생성된 id, userId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt, children(기본 '기타' 하나)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "추가 예",
                    value = "{\"name\":\"보컬로이드\",\"description\":\"설명(선택)\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성된 id, userId, name, description, thumbnailUrl(없으면 생략), createdAt, updatedAt, children(기본 '기타' 하나)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 중복, 입력값 오류")
    })
    ApiResponse<GenreCharacterGroupVO> create(GenreCharacterGroupVO request);

    @Operation(
            summary = "장르 이름과 설명 수정 (예: 보컬로이드)",
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
            @Parameter(description = "수정할 장르 id", example = "1") Long genreCharacterGroupId,
            GenreCharacterGroupVO request);

    @Operation(
            summary = "장르 삭제, 하위도 함께 삭제되고 쓰던 상품은 '기타'로 이동 (예: 보컬로이드)",
            description = "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기타 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "삭제할 장르 id", example = "1") Long genreCharacterGroupId);

    @Operation(
            summary = "장르 삭제 전 영향 상품 수 조회 (예: 보컬로이드)",
            description = "응답: count: 이 장르의 하위를 쓰는 상품 수")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "count: 이 장르의 하위를 쓰는 상품 수"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<UsageResponseVO> getUsage(
            @Parameter(description = "조회할 장르 id", example = "1") Long genreCharacterGroupId);
}
