package com.jong.mikumikureservationbackend.mapper.post;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostEditorImageMapMapper {

    int insertAllByFilePaths(@Param("userId") Long userId, @Param("postId") Long postId,
                             @Param("filePaths") List<String> filePaths);

    int deleteByPost(@Param("userId") Long userId, @Param("postId") Long postId);
}
