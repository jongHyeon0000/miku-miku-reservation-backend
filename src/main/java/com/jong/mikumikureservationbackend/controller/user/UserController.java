package com.jong.mikumikureservationbackend.controller.user;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.user.doc.UserControllerDocs;
import com.jong.mikumikureservationbackend.service.post.PostCommentService;
import com.jong.mikumikureservationbackend.service.post.PostService;
import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping("/{userId}/post")
    public ApiResponse<List<PostVO>> getRecentPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", postService.getRecentByUser(currentUser.getId(), userId, size));
    }

    @Override
    @GetMapping("/{userId}/comment")
    public ApiResponse<List<PostCommentVO>> getRecentComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", postCommentService.getRecentByUser(userId, size));
    }
}
