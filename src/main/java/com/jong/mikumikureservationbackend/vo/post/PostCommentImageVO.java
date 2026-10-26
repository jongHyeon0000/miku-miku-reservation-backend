package com.jong.mikumikureservationbackend.vo.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 댓글 이미지 (테이블 post_comment_image)
 * 업로드 결과와 댓글 응답의 이미지 목록에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "댓글 이미지. 업로드 결과와 댓글의 이미지 목록에 같이 쓴다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCommentImageVO {

    /** PK */
    @Schema(description = "댓글 이미지 id", example = "31")
    private Long postCommentImageId;

    /** 소유 계정 */
    @Schema(description = "소유 계정 id", hidden = true)
    private Long userId;

    /** 댓글 (업로드 직후는 NULL, 댓글 저장 때 채움, NULL로 1일 지나면 고아 정리) */
    @Schema(description = "댓글 id", hidden = true)
    private Long postCommentId;

    /** 표시 순서 (1, 2), 댓글에 연결될 때 채움 */
    @Schema(description = "표시 순서", hidden = true)
    private Integer sortOrder;

    /** 서버 저장 경로 */
    @Schema(description = "서버 저장 경로 (업로드 결과에만 나온다)")
    private String filePath;

    /** 원본 파일명 */
    @Schema(description = "원본 파일명 (업로드 결과에만 나온다)", example = "photo.png")
    private String originalName;

    /** 파일 크기(byte) */
    @Schema(description = "파일 크기(byte) (업로드 결과에만 나온다)", example = "102400")
    private Long fileSize;

    /** 업로드일 (고아 파일 정리 기준) */
    @Schema(description = "업로드일 (고아 파일 정리 기준)", hidden = true)
    private LocalDateTime createdAt;

    /** [response] 이미지를 내려주는 주소 (/uploads/ + file_path) */
    @Schema(description = "이미지를 내려주는 주소 (/uploads/ + file_path)", example = "/uploads/1/comment-image/2026/10/uuid.png")
    private String url;
}
