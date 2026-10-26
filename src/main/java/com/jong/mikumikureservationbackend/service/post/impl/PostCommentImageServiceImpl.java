package com.jong.mikumikureservationbackend.service.post.impl;

import com.jong.mikumikureservationbackend.common.constant.UploadConstant;
import com.jong.mikumikureservationbackend.mapper.post.PostCommentImageMapper;
import com.jong.mikumikureservationbackend.service.common.ImageFileStore;
import com.jong.mikumikureservationbackend.service.post.PostCommentImageService;
import com.jong.mikumikureservationbackend.vo.post.PostCommentImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentImageServiceImpl implements PostCommentImageService {

    private final PostCommentImageMapper postCommentImageMapper;
    private final ImageFileStore imageFileStore;

    @Override
    @Transactional
    public PostCommentImageVO upload(Long userId, MultipartFile file) {
        imageFileStore.validate(file);

        String originalName = imageFileStore.originalNameOf(file);
        String extension = imageFileStore.extensionOf(originalName);

        String filePath = buildFilePath(userId, extension);

        postCommentImageMapper.insert(userId, filePath, originalName, file.getSize());

        Long postCommentImageId = postCommentImageMapper.selectLastInsertId();

        imageFileStore.save(file, filePath);   // 실패하면 예외로 DB 행도 롤백된다

        PostCommentImageVO result = new PostCommentImageVO();

        result.setPostCommentImageId(postCommentImageId);
        result.setFilePath(filePath);
        result.setOriginalName(originalName);
        result.setFileSize(file.getSize());
        result.setUrl(UploadConstant.URL_PREFIX + filePath);

        return result;
    }

    /* 저장 경로는 서버가 만든다: {userId}/comment-image/{yyyy}/{MM}/{UUID}.{확장자} (원본 파일명은 경로에 쓰지 않는다) */
    private String buildFilePath(Long userId, String extension) {
        LocalDate today = LocalDate.now();

        return String.format("%d/%s/%d/%02d/%s.%s", userId, UploadConstant.COMMENT_IMAGE_DIR, today.getYear(),
                today.getMonthValue(), UUID.randomUUID(), extension);
    }
}
