package com.jong.mikumikureservationbackend.service.post.impl;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.PostStatus;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.post.PostCommentImageMapper;
import com.jong.mikumikureservationbackend.mapper.post.PostCommentMapper;
import com.jong.mikumikureservationbackend.mapper.post.PostMapper;
import com.jong.mikumikureservationbackend.mapper.user.UserMapper;
import com.jong.mikumikureservationbackend.service.post.PostCommentService;
import com.jong.mikumikureservationbackend.vo.post.PostCommentImageVO;
import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentMapper postCommentMapper;
    private final PostCommentImageMapper postCommentImageMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    public List<PostCommentVO> getList(Long userId, Long postId) {
        checkPostExists(postId);

        List<PostCommentVO> comments = postCommentMapper.selectByPost(userId, postId);
        Map<Long, PostCommentVO> commentById = new HashMap<>();
        List<PostCommentVO> topLevel = new ArrayList<>();

        for (PostCommentVO comment : comments) {
            comment.setReplies(new ArrayList<>());
            commentById.put(comment.getPostCommentId(), comment);
        }

        attachImages(comments);

        for (PostCommentVO comment : comments) {
            if (comment.getParentPostCommentId() == null) {
                topLevel.add(comment);
            } else {
                commentById.get(comment.getParentPostCommentId()).getReplies().add(comment);
            }
            if (comment.getStatus() != PostStatus.NORMAL) {
                hideContent(comment);
            }
        }

        return topLevel;
    }

    @Override
    @Transactional
    public PostCommentVO create(Long userId, Long postId, PostCommentVO request) {
        checkPostExists(postId);
        checkParentAndMention(postId, request.getParentPostCommentId(), request.getMentionUserId());

        List<Long> imageIds = distinct(request.getPostCommentImageIds());

        postCommentMapper.insert(userId, postId, request.getParentPostCommentId(), request.getMentionUserId(),
                request.getContent().trim());

        Long postCommentId = postCommentMapper.selectLastInsertId();

        attachImageIds(userId, postCommentId, imageIds);

        return getOne(userId, postCommentId);
    }

    @Override
    @Transactional
    public PostCommentVO update(Long userId, Long postCommentId, PostCommentVO request) {
        checkOwner(userId, postCommentId);

        List<Long> imageIds = distinct(request.getPostCommentImageIds());

        postCommentMapper.update(userId, postCommentId, request.getContent().trim());
        postCommentImageMapper.detachAllByComment(userId, postCommentId);
        attachImageIds(userId, postCommentId, imageIds);

        return getOne(userId, postCommentId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long postCommentId) {
        checkOwner(userId, postCommentId);

        postCommentMapper.softDelete(userId, postCommentId);
    }

    @Override
    @Transactional
    public void withdrawByUser(Long userId) {
        postCommentMapper.withdrawByUser(userId);
    }

    /* 없거나 삭제, 탈퇴한 글은 404 */
    private void checkPostExists(Long postId) {
        PostVO post = postMapper.selectOwnerAndStatus(postId);

        if (post == null || post.getStatus() != PostStatus.NORMAL) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
    }

    /* 없거나 삭제, 탈퇴한 댓글은 404, 남의 댓글은 403 */
    private void checkOwner(Long userId, Long postCommentId) {
        PostCommentVO comment = postCommentMapper.selectBrief(postCommentId);

        if (comment == null || comment.getStatus() != PostStatus.NORMAL) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(GlobalExceptionCode.FORBIDDEN);
        }
    }

    /* 답글의 부모는 이 글의 최상위 댓글이어야 한다(부모가 삭제된 상태여도 된다). 멘션은 답글에만 쓰고 대상은 활성 사용자여야 한다 */
    private void checkParentAndMention(Long postId, Long parentPostCommentId, Long mentionUserId) {
        if (parentPostCommentId == null) {
            if (mentionUserId != null) {
                throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "mentionUserId는 답글에만 쓸 수 있습니다");
            }

            return;
        }

        PostCommentVO parent = postCommentMapper.selectBrief(parentPostCommentId);

        if (parent == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        if (!parent.getPostId().equals(postId) || parent.getParentPostCommentId() != null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST,
                    "parentPostCommentId는 이 글의 최상위 댓글이어야 합니다");
        }
        if (mentionUserId != null && userMapper.selectById(mentionUserId) == null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "mentionUserId에 해당하는 사용자가 없습니다");
        }
    }

    /* 배열 순서대로 1, 2번으로 연결한다. 내가 올리지 않았거나 이미 다른 댓글에 쓰인 이미지면 404(전체 롤백) */
    private void attachImageIds(Long userId, Long postCommentId, List<Long> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            if (postCommentImageMapper.attach(userId, imageIds.get(i), postCommentId, i + 1) == 0) {
                throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
            }
        }
    }

    private PostCommentVO getOne(Long userId, Long postCommentId) {
        PostCommentVO comment = postCommentMapper.selectById(userId, postCommentId);

        comment.setReplies(new ArrayList<>());
        attachImages(Collections.singletonList(comment));

        return comment;
    }

    /* 정상 댓글의 이미지를 한 번에 조회해서 붙인다(없으면 빈 배열) */
    private void attachImages(List<PostCommentVO> comments) {
        List<Long> commentIds = new ArrayList<>();

        for (PostCommentVO comment : comments) {
            if (comment.getStatus() == PostStatus.NORMAL) {
                commentIds.add(comment.getPostCommentId());
                comment.setImages(new ArrayList<>());
            }
        }
        if (commentIds.isEmpty()) {
            return;
        }

        Map<Long, PostCommentVO> commentById = new HashMap<>();

        for (PostCommentVO comment : comments) {
            commentById.put(comment.getPostCommentId(), comment);
        }
        for (PostCommentImageVO image : postCommentImageMapper.selectByCommentIds(commentIds)) {
            commentById.get(image.getPostCommentId()).getImages().add(image);
            image.setPostCommentId(null);
        }
    }

    /* 삭제, 탈퇴한 댓글은 응답에 내용이 나가지 않게 postCommentId, parentPostCommentId, status, createdAt, replies만 남긴다 */
    private void hideContent(PostCommentVO comment) {
        comment.setUserId(null);
        comment.setAuthorName(null);
        comment.setMentionUserId(null);
        comment.setMentionUserName(null);
        comment.setContent(null);
        comment.setUpdatedAt(null);
        comment.setMine(null);
        comment.setImages(null);
    }

    private List<Long> distinct(List<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(new LinkedHashSet<>(ids));
    }
}
