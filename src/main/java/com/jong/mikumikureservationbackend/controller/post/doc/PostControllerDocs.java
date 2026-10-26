package com.jong.mikumikureservationbackend.controller.post.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/* PostController의 Swagger 설명 전용 인터페이스. 로직은 PostController에만 둔다 */
@Tag(name = "커뮤니티 게시글", description = "자유게시판 글. 모든 사용자의 글을 볼 수 있고 수정, 삭제는 본인 글만 된다. 삭제, 탈퇴한 글은 행을 지우지 않고 status로 구분하며 내용은 응답에 나가지 않는다.")
public interface PostControllerDocs {

    @Operation(
            summary = "게시글 목록 조회 (예: 최신 글 10개)",
            description = "쿼리: page: 페이지 번호 (1부터, 기본 1), size: 한 페이지 개수 (기본 10)\n\n"
                    + "응답: items(postId, title, authorName, viewCount, commentCount, createdAt, status, mine), page, size, totalCount, totalPages. 최신순이고 본문은 없다. "
                    + "삭제(DELETED), 탈퇴(WITHDRAWN)한 글은 자리에 남지만 postId, status, createdAt만 나온다")
    ApiResponse<PageResponseVO<PostVO>> getPage(
            @Parameter(description = "페이지 번호 (1부터)", example = "1") int page,
            @Parameter(description = "한 페이지 개수 (기본 10)", example = "10") int size);

    @Operation(
            summary = "게시글 상세 조회 (예: 글 열기, 조회수 +1)",
            description = "호출할 때마다 조회수가 1 오르고 응답의 viewCount는 오른 뒤 값이다.\n\n"
                    + "응답: postId, title, content(에디터 HTML), authorName, viewCount, commentCount, createdAt, updatedAt, status, mine")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "postId, title, content, authorName, viewCount, commentCount, createdAt, updatedAt, status, mine"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 글, 삭제 또는 탈퇴한 글")
    })
    ApiResponse<PostVO> getDetail(
            @Parameter(description = "조회할 게시글 id", example = "12") Long postId);

    @Operation(
            summary = "게시글 등록 (예: 새 글 쓰기)",
            description = "요청 본문: title: 제목 (필수, 200자 이하), content: 본문 에디터 HTML (필수, base64 이미지는 불가, 이미지는 에디터 이미지 업로드로 올리고 받은 url을 img src에 넣는다)\n\n"
                    + "응답: 등록된 글(상세와 같은 모양, viewCount 0, mine true)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "등록 예",
                    value = "{\"title\":\"이번 달 예약 마감 정리\",\"content\":\"<p>본문</p><p><img src=\\\"/uploads/1/editor-image/2026/10/abc.png\\\"></p>\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록된 글 (상세와 같은 모양)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "제목, 본문 빈 값 또는 길이 초과, 본문에 base64 이미지")
    })
    ApiResponse<PostVO> create(PostVO request);

    @Operation(
            summary = "게시글 수정 (예: 제목, 본문 고치기)",
            description = "요청 본문은 등록과 같다(title, content 모두 보낸다). 본문에서 뺀 이미지는 글과의 연결이 풀리고 1일 뒤 자동 정리된다.\n\n"
                    + "응답: 수정된 글(상세와 같은 모양, 조회수는 오르지 않는다)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "수정 예",
                    value = "{\"title\":\"수정한 제목\",\"content\":\"<p>수정한 본문</p>\"}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정된 글 (상세와 같은 모양)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "제목, 본문 빈 값 또는 길이 초과, 본문에 base64 이미지"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "남의 글"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 글, 삭제 또는 탈퇴한 글")
    })
    ApiResponse<PostVO> update(
            @Parameter(description = "수정할 게시글 id", example = "12") Long postId,
            PostVO request);

    @Operation(
            summary = "게시글 삭제 (예: 내 글 지우기)",
            description = "행을 지우지 않고 status를 DELETED로 바꾼다(내용, 이미지는 보관, 응답에는 나가지 않는다). 그 글의 댓글은 그대로 남지만 글이 삭제되어 댓글 목록은 조회되지 않는다.\n\n"
                    + "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "남의 글"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 글, 이미 삭제 또는 탈퇴한 글")
    })
    ApiResponse<Void> delete(
            @Parameter(description = "삭제할 게시글 id", example = "12") Long postId);
}
