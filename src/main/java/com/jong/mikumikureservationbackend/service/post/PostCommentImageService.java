package com.jong.mikumikureservationbackend.service.post;

import com.jong.mikumikureservationbackend.vo.post.PostCommentImageVO;
import org.springframework.web.multipart.MultipartFile;

public interface PostCommentImageService {

    /*
     * 이미지를 검사하고 저장한다. 이 시점에는 댓글과 연결하지 않는다(댓글을 저장할 때 id로 연결, 연결 없이 1일이 지나면 정리).
     * 확장자와 파일 내용(앞부분 바이트)이 모두 허용된 이미지여야 한다.
     */
    PostCommentImageVO upload(Long userId, MultipartFile file);
}
