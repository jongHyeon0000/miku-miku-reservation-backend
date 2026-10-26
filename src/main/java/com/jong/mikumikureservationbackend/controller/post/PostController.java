package com.jong.mikumikureservationbackend.controller.post;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.post.doc.PostControllerDocs;
import com.jong.mikumikureservationbackend.service.post.PostService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs {

    private final PostService postService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<PageResponseVO<PostVO>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", postService.getPage(currentUser.getId(), page, size));
    }

    @Override
    @GetMapping("/{postId}")
    public ApiResponse<PostVO> getDetail(
            @PathVariable Long postId) {
        return ApiResponse.ok("조회가 완료되었습니다.", postService.getDetail(currentUser.getId(), postId));
    }

    @Override
    @PostMapping
    public ApiResponse<PostVO> create(@Valid @RequestBody PostVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", postService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{postId}")
    public ApiResponse<PostVO> update(
            @PathVariable Long postId,
            @Valid @RequestBody PostVO request) {
        return ApiResponse.ok("수정이 완료되었습니다.", postService.update(currentUser.getId(), postId, request));
    }

    @Override
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @PathVariable Long postId) {
        postService.delete(currentUser.getId(), postId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }
}
