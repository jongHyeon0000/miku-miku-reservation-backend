package com.jong.mikumikureservationbackend.vo.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jong.mikumikureservationbackend.common.constant.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 (테이블 post)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "커뮤니티 게시글. 요청, 응답에 같이 쓴다. 삭제, 탈퇴 상태이면 내용 없이 postId, status, createdAt만 나온다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostVO {

    /** PK */
    @Schema(description = "게시글 id", example = "12")
    private Long postId;

    /** 작성자 */
    @Schema(description = "작성자 계정 id. 요청 본문에 보내도 읽지 않는다", example = "1")
    private Long userId;

    /** 제목 */
    @Schema(description = "제목 (200자 이하)", example = "이번 달 예약 마감 정리", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 200)
    private String title;

    /** 본문 (에디터 HTML) */
    @Schema(description = "본문 (에디터 HTML). 목록에는 나오지 않는다. base64 이미지는 넣을 수 없다", example = "<p>본문</p>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String content;

    /** 조회수 (상세 조회 때마다 +1, updated_at은 갱신하지 않음) */
    @Schema(description = "조회수", example = "35")
    private Integer viewCount;

    /** 상태: NORMAL 정상, DELETED 본인이 삭제, WITHDRAWN 작성자가 탈퇴 (내용은 보관하고 응답에서만 가림) */
    @Schema(description = "상태: NORMAL 정상, DELETED 본인이 삭제, WITHDRAWN 작성자가 탈퇴", example = "NORMAL")
    private PostStatus status;

    /** 본인이 삭제한 일시 (DELETED일 때만) */
    @Schema(description = "삭제일시", hidden = true)
    private LocalDateTime deletedAt;

    /** 작성일 */
    @Schema(description = "작성일시", example = "2026-10-26T10:30:00")
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정일시 (상세에만 나온다)", example = "2026-10-26T10:30:00")
    private LocalDateTime updatedAt;

    /** [users joined] 작성자 이름 */
    @Schema(description = "작성자 이름", example = "미쿠")
    private String authorName;

    /** [response] 댓글 수 (삭제, 탈퇴한 댓글 포함 전체) */
    @Schema(description = "댓글 수 (삭제, 탈퇴한 댓글 포함 전체, 목록에만 나온다)", example = "5")
    private Integer commentCount;

    /** [response] 내가 쓴 글인지 */
    @Schema(description = "내가 쓴 글인지", example = "false")
    private Boolean mine;
}
