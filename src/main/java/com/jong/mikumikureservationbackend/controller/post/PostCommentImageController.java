package com.jong.mikumikureservationbackend.controller.post;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.post.doc.PostCommentImageControllerDocs;
import com.jong.mikumikureservationbackend.service.post.PostCommentImageService;
import com.jong.mikumikureservationbackend.vo.post.PostCommentImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload/comment-image")
@RequiredArgsConstructor
public class PostCommentImageController implements PostCommentImageControllerDocs {

    private final PostCommentImageService postCommentImageService;
    private final CurrentUser currentUser;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCommentImageVO> upload(
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("이미지 업로드가 완료되었습니다.", postCommentImageService.upload(currentUser.getId(), file));
    }
}
