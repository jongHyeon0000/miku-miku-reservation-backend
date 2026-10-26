package com.jong.mikumikureservationbackend.controller.editor.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

/* EditorImageController의 Swagger 설명 전용 인터페이스. 로직은 EditorImageController에만 둔다 */
@Tag(name = "에디터 이미지", description = "상품 설명과 커뮤니티 글 본문(HTML)에 넣는 이미지 업로드. 상품이나 글에 쓰이지 않은 이미지는 1일 뒤 매일 00시에 자동 정리된다.")
public interface EditorImageControllerDocs {

    @Operation(
            summary = "에디터 이미지 업로드 (예: 상품 설명이나 글에 넣을 사진)",
            description = "응답: editorImageId, url(에디터가 이미지 src로 쓸 /uploads/ 주소)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "editorImageId, url(에디터가 이미지 src로 쓸 /uploads/ 주소)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과")
    })
    ApiResponse<EditorImageVO> upload(
            @Parameter(description = "업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)") MultipartFile file);
}
