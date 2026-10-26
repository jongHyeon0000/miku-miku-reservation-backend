package com.jong.mikumikureservationbackend.controller.post;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.post.doc.PostCommentControllerDocs;
import com.jong.mikumikureservationbackend.service.post.PostCommentService;
import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;
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

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostCommentController implements PostCommentControllerDocs {

    private final PostCommentService postCommentService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping("/{postId}/comment")
    public ApiResponse<List<PostCommentVO>> getList(
            @PathVariable Long postId) {
        return ApiResponse.ok("조회가 완료되었습니다.", postCommentService.getList(currentUser.getId(), postId));
    }

    @Override
    @PostMapping("/{postId}/comment")
    public ApiResponse<PostCommentVO> create(
            @PathVariable Long postId,
            @Valid @RequestBody PostCommentVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", postCommentService.create(currentUser.getId(), postId, request));
    }

    @Override
    @PutMapping("/comment/{postCommentId}")
    public ApiResponse<PostCommentVO> update(
            @PathVariable Long postCommentId,
            @Valid @RequestBody PostCommentVO request) {
        return ApiResponse.ok("수정이 완료되었습니다.", postCommentService.update(currentUser.getId(), postCommentId, request));
    }

    @Override
    @DeleteMapping("/comment/{postCommentId}")
    public ApiResponse<Void> delete(
            @PathVariable Long postCommentId) {
        postCommentService.delete(currentUser.getId(), postCommentId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }
}
