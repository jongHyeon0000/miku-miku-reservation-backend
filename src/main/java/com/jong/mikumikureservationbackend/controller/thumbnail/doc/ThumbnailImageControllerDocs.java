package com.jong.mikumikureservationbackend.controller.thumbnail.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.thumbnail.ThumbnailUrlResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

/* ThumbnailImageController의 Swagger 설명 전용 인터페이스. 로직은 ThumbnailImageController에만 둔다 */
@Tag(name = "썸네일 이미지", description = "상품, 분류 9종, 내 프로필 공통 썸네일. 대상당 1개이며 올리면 교체된다.")
public interface ThumbnailImageControllerDocs {

    @Operation(
            summary = "썸네일 업로드와 교체 (예: 상품 대표 사진, 판매처 채널 로고)",
            description = "응답: thumbnailUrl: 이미지를 내려주는 /uploads/ 주소")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "thumbnailUrl: 이미지를 내려주는 /uploads/ 주소"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "알 수 없는 대상 종류, 파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<ThumbnailUrlResponseVO> upload(
            @Parameter(description = "썸네일을 붙일 대상 종류 (경로): figure, shop-group, shop, figure-type-group, figure-type, series-group, series, genre-character-group, genre-character, maker, user(내 프로필, targetId는 내 계정 id)", example = "figure") String targetType,
            @Parameter(description = "대상 id", example = "1") Long targetId,
            @Parameter(description = "업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)") MultipartFile file);

    @Operation(
            summary = "썸네일 제거 (예: 대표 사진 빼기)",
            description = "응답: 성공 시 data 없음 (썸네일이 없어도 성공)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음 (썸네일이 없어도 성공)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "알 수 없는 대상 종류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "썸네일을 지울 대상 종류", example = "figure") String targetType,
            @Parameter(description = "대상 id", example = "1") Long targetId);
}
