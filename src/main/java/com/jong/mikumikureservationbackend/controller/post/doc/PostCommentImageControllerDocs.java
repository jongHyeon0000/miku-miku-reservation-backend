package com.jong.mikumikureservationbackend.controller.post.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.post.PostCommentImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

/* PostCommentImageController의 Swagger 설명 전용 인터페이스. 로직은 PostCommentImageController에만 둔다 */
@Tag(name = "댓글 이미지", description = "댓글에 붙이는 이미지 업로드. 댓글에 연결되지 않은 이미지는 1일 뒤 매일 00시 10분에 자동 정리된다.")
public interface PostCommentImageControllerDocs {

    @Operation(
            summary = "댓글 이미지 업로드 (예: 댓글에 붙일 사진)",
            description = "댓글을 쓰기 전에 이미지를 한 장씩 올려 postCommentImageId를 받고, 댓글 등록 때 postCommentImageIds로 보낸다.\n\n"
                    + "응답: postCommentImageId, url(미리보기에 쓸 /uploads/ 주소), filePath, originalName, fileSize")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "postCommentImageId, url(미리보기에 쓸 /uploads/ 주소), filePath, originalName, fileSize"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "파일 없음, 허용하지 않는 확장자, 파일 내용이 확장자와 다름, 크기 초과")
    })
    ApiResponse<PostCommentImageVO> upload(
            @Parameter(description = "업로드할 이미지 파일 (본문 multipart, 필드명 file, 필수, jpg/jpeg/png/gif/webp, 최대 5MB)") MultipartFile file);
}
