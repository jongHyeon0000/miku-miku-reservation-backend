package com.jong.mikumikureservationbackend.service.post.impl;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.PostStatus;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.post.PostEditorImageMapMapper;
import com.jong.mikumikureservationbackend.mapper.post.PostMapper;
import com.jong.mikumikureservationbackend.service.editor.EditorImageService;
import com.jong.mikumikureservationbackend.service.post.PostService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostEditorImageMapMapper postEditorImageMapMapper;
    private final EditorImageService editorImageService;

    @Override
    public PageResponseVO<PostVO> getPage(Long userId, int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(GlobalExceptionCode.VALIDATION_FAILED, "page, size는 1 이상이어야 합니다");
        }

        long offset = (long) (page - 1) * size;
        long totalCount = postMapper.countAll();
        List<PostVO> items = postMapper.selectPage(userId, offset, size);

        for (PostVO item : items) {
            if (item.getStatus() != PostStatus.NORMAL) {
                hideContent(item);
            }
        }

        return new PageResponseVO<>(items, page, size, totalCount);
    }

    @Override
    @Transactional
    public PostVO getDetail(Long userId, Long postId) {
        PostVO post = postMapper.selectById(userId, postId);

        if (post == null || post.getStatus() != PostStatus.NORMAL) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }

        postMapper.increaseViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);

        return post;
    }

    @Override
    @Transactional
    public PostVO create(Long userId, PostVO request) {
        editorImageService.checkNoBase64(request.getContent());

        postMapper.insert(userId, request.getTitle().trim(), request.getContent());

        Long postId = postMapper.selectLastInsertId();

        linkEditorImages(userId, postId, request.getContent());

        return postMapper.selectById(userId, postId);
    }

    @Override
    @Transactional
    public PostVO update(Long userId, Long postId, PostVO request) {
        checkOwner(userId, postId);
        editorImageService.checkNoBase64(request.getContent());

        postMapper.update(userId, postId, request.getTitle().trim(), request.getContent());
        postEditorImageMapMapper.deleteByPost(userId, postId);
        linkEditorImages(userId, postId, request.getContent());

        return postMapper.selectById(userId, postId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long postId) {
        checkOwner(userId, postId);

        postMapper.softDelete(userId, postId);
    }

    @Override
    @Transactional
    public void withdrawByUser(Long userId) {
        postMapper.withdrawByUser(userId);
    }

    /* 없거나 삭제, 탈퇴한 글은 404, 남의 글은 403 */
    private void checkOwner(Long userId, Long postId) {
        PostVO post = postMapper.selectOwnerAndStatus(postId);

        if (post == null || post.getStatus() != PostStatus.NORMAL) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(GlobalExceptionCode.FORBIDDEN);
        }
    }

    /* 삭제, 탈퇴한 글은 응답에 내용이 나가지 않게 postId, status, createdAt만 남긴다 */
    private void hideContent(PostVO post) {
        post.setTitle(null);
        post.setAuthorName(null);
        post.setAuthorThumbnailUrl(null);
        post.setViewCount(null);
        post.setCommentCount(null);
        post.setMine(null);
    }

    /* 본문에 쓰인 에디터 이미지를 이 글에 연결한다. 업로드 경로가 정확히 같은 이미지만 연결하고 외부 주소는 무시한다 */
    private void linkEditorImages(Long userId, Long postId, String content) {
        List<String> filePaths = editorImageService.extractFilePaths(content);

        if (!filePaths.isEmpty()) {
            postEditorImageMapMapper.insertAllByFilePaths(userId, postId, filePaths);
        }
    }
}
