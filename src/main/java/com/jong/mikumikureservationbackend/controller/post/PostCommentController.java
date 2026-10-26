package com.jong.mikumikureservationbackend.controller.post;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.service.post.PostCommentService;
import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "커뮤니티 댓글", description = "게시글의 댓글과 답글(한 단계). 수정, 삭제는 본인 것만 된다. 삭제, 탈퇴한 댓글은 행을 지우지 않고 status로 구분하며 목록에는 내용 없이 자리만 남는다.")
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "댓글 목록 조회 (예: 글의 댓글과 답글 전체)",
            description = "페이지 없이 그 글의 댓글 전체를 준다. 최상위 댓글은 오래된 것이 먼저이고 각 replies(답글, 오래된 것이 먼저)에 답글이 담긴다.\n\n"
                    + "응답: 최상위 댓글 배열. 항목: postCommentId, parentPostCommentId(답글만), userId, authorName, mentionUserId와 mentionUserName(멘션이 있고 대상이 탈퇴하지 않았을 때만), content, images(postCommentImageId, url), createdAt, updatedAt, status, mine, replies. "
                    + "삭제(DELETED), 탈퇴(WITHDRAWN)한 댓글은 그 자리에 남지만 postCommentId, parentPostCommentId, status, createdAt, replies만 나온다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "최상위 댓글 배열 (답글은 replies 안)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 글, 삭제 또는 탈퇴한 글")
    })
    @GetMapping("/{postId}/comment")
    public ApiResponse<List<PostCommentVO>> getList(
            @Parameter(description = "게시글 id", example = "12") @PathVariable Long postId) {
        return ApiResponse.ok("조회가 완료되었습니다.", postCommentService.getList(currentUser.getId(), postId));
    }

    @Operation(
            summary = "댓글, 답글 등록 (예: 글에 댓글 달기, 댓글에 답글 달기)",
            description = "요청 본문: content: 내용 (필수, 1000자 이하 일반 텍스트), parentPostCommentId: 답글일 때만 최상위 댓글 id (답글의 답글도 최상위 댓글 id를 보낸다), "
                    + "mentionUserId: 답글일 때만 누구에게 다는 답글인지 (대상 댓글의 userId), postCommentImageIds: 댓글 이미지 업로드로 받은 id 0~2개 (배열 순서가 표시 순서)\n\n"
                    + "응답: 등록된 댓글 한 건 (replies는 빈 배열)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "댓글 예", value = "{\"content\":\"정리 감사합니다\",\"postCommentImageIds\":[31]}"),
            @ExampleObject(name = "답글 예", value = "{\"content\":\"도움이 됐다니 다행이에요\",\"parentPostCommentId\":101,\"mentionUserId\":3}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록된 댓글 한 건"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "내용 빈 값 또는 길이 초과, 이미지 3개 이상, 부모가 이 글의 최상위 댓글이 아님, 최상위 댓글에 mentionUserId, 없는 mentionUserId"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 글, 삭제 또는 탈퇴한 글, 없는 부모 댓글, 남의 이미지 id 또는 이미 쓰인 이미지 id")
    })
    @PostMapping("/{postId}/comment")
    public ApiResponse<PostCommentVO> create(
            @Parameter(description = "댓글을 달 게시글 id", example = "12") @PathVariable Long postId,
            @Valid @RequestBody PostCommentVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", postCommentService.create(currentUser.getId(), postId, request));
    }

    @Operation(
            summary = "댓글, 답글 수정 (예: 내용, 이미지 고치기)",
            description = "요청 본문: content: 내용 (필수, 1000자 이하), postCommentImageIds: 수정 후 최종 이미지 id 목록 (최대 2개, 지금 붙은 이미지를 유지하려면 기존 id를 다시 보내고, 생략하거나 빈 배열이면 이미지를 모두 지운다. 목록에 없는 기존 이미지는 연결이 풀려 1일 뒤 자동 정리된다)\n\n"
                    + "부모(parentPostCommentId)와 멘션(mentionUserId)은 바꿀 수 없다(보내도 무시). 응답: 수정된 댓글 한 건 (replies는 빈 배열)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "수정 예", value = "{\"content\":\"정리 정말 감사합니다\",\"postCommentImageIds\":[31,35]}")
    }))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정된 댓글 한 건"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "내용 빈 값 또는 길이 초과, 이미지 3개 이상"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "남의 댓글"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 댓글, 삭제 또는 탈퇴한 댓글, 남의 이미지 id 또는 다른 댓글에 쓰인 이미지 id")
    })
    @PutMapping("/comment/{postCommentId}")
    public ApiResponse<PostCommentVO> update(
            @Parameter(description = "수정할 댓글 id", example = "101") @PathVariable Long postCommentId,
            @Valid @RequestBody PostCommentVO request) {
        return ApiResponse.ok("수정이 완료되었습니다.", postCommentService.update(currentUser.getId(), postCommentId, request));
    }

    @Operation(
            summary = "댓글, 답글 삭제 (예: 내 댓글 지우기)",
            description = "행을 지우지 않고 status를 DELETED로 바꾼다(내용, 이미지는 보관, 응답에는 나가지 않는다). 최상위 댓글을 지워도 답글은 그대로 남고 그 자리만 삭제 표시가 된다.\n\n"
                    + "응답: 성공 시 data 없음")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 시 data 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "남의 댓글"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 댓글, 이미 삭제 또는 탈퇴한 댓글")
    })
    @DeleteMapping("/comment/{postCommentId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "삭제할 댓글 id", example = "101") @PathVariable Long postCommentId) {
        postCommentService.delete(currentUser.getId(), postCommentId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }
}
