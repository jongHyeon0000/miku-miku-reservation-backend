package com.jong.mikumikureservationbackend.mapper.post;

import com.jong.mikumikureservationbackend.vo.post.PostCommentImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostCommentImageMapper {

    int insert(@Param("userId") Long userId, @Param("filePath") String filePath,
               @Param("originalName") String originalName, @Param("fileSize") Long fileSize);

    Long selectLastInsertId();

    List<PostCommentImageVO> selectByCommentIds(@Param("postCommentIds") List<Long> postCommentIds);

    int detachAllByComment(@Param("userId") Long userId, @Param("postCommentId") Long postCommentId);

    int attach(@Param("userId") Long userId, @Param("postCommentImageId") Long postCommentImageId,
               @Param("postCommentId") Long postCommentId, @Param("sortOrder") int sortOrder);

    List<PostCommentImageVO> selectOrphans(@Param("retentionDays") int retentionDays);

    int deleteOrphanById(@Param("postCommentImageId") Long postCommentImageId);
}
