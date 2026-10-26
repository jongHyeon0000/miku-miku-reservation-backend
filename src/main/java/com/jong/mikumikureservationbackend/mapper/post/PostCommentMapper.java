package com.jong.mikumikureservationbackend.mapper.post;

import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/* 커뮤니티는 모든 사용자가 조회하므로 조회에는 user_id 조건이 없다(userId는 mine 계산용). 수정과 삭제는 서비스가 소유자를 확인한 뒤 user_id 조건으로 한 번 더 막는다 */
@Mapper
public interface PostCommentMapper {

    List<PostCommentVO> selectByPost(@Param("userId") Long userId, @Param("postId") Long postId);

    PostCommentVO selectById(@Param("userId") Long userId, @Param("postCommentId") Long postCommentId);

    PostCommentVO selectBrief(@Param("postCommentId") Long postCommentId);

    int insert(@Param("userId") Long userId, @Param("postId") Long postId,
               @Param("parentPostCommentId") Long parentPostCommentId, @Param("mentionUserId") Long mentionUserId,
               @Param("content") String content);

    Long selectLastInsertId();

    int update(@Param("userId") Long userId, @Param("postCommentId") Long postCommentId,
               @Param("content") String content);

    int softDelete(@Param("userId") Long userId, @Param("postCommentId") Long postCommentId);

    int withdrawByUser(@Param("userId") Long userId);
}
