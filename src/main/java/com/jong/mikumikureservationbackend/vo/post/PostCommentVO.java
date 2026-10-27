package com.jong.mikumikureservationbackend.vo.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jong.mikumikureservationbackend.common.constant.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 커뮤니티 댓글, 답글 (테이블 post_comment)
 * 요청, 응답, 매퍼 조회 결과에 같이 쓴다. [테이블명 joined](조인한 테이블), [request](요청 전용), [response](응답 전용) 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "커뮤니티 댓글, 답글. 요청, 응답에 같이 쓴다. 삭제, 탈퇴 상태이면 내용 없이 postCommentId, parentPostCommentId, status, createdAt, replies만 나온다")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCommentVO {

    /** PK */
    @Schema(description = "댓글 id", example = "101")
    private Long postCommentId;

    /** 게시글 */
    @Schema(description = "게시글 id", hidden = true)
    private Long postId;

    /** 작성자 */
    @Schema(description = "작성자 계정 id. 이 사람에게 답글을 달 때 mentionUserId로 쓴다. 요청 본문에 보내도 읽지 않는다", example = "3")
    private Long userId;

    /** 부모 댓글 (항상 최상위 댓글, NULL이면 최상위, 답글은 한 단계) */
    @Schema(description = "부모(최상위) 댓글 id. 답글일 때만 보내고, 답글의 답글도 최상위 댓글 id를 보낸다. 최상위 댓글이면 없다", example = "101")
    private Long parentPostCommentId;

    /** 답글 대상 사용자 (탈퇴하면 응답에서 빠진다) */
    @Schema(description = "답글 대상 사용자 id (답글에만, 대상 댓글의 userId). 대상이 탈퇴했으면 응답에서 빠진다", example = "3")
    private Long mentionUserId;

    /** 내용 (일반 텍스트, 멘션 글자는 넣지 않음) */
    @Schema(description = "내용 (일반 텍스트, 1000자 이하, 이미지가 있으면 생략 가능). 멘션 글자(@이름)는 넣지 않는다", example = "정리 감사합니다")
    @Size(max = 1000)
    private String content;

    /** 상태: NORMAL 정상, DELETED 본인이 삭제, WITHDRAWN 작성자가 탈퇴 (내용은 보관하고 응답에서만 가림) */
    @Schema(description = "상태: NORMAL 정상, DELETED 본인이 삭제, WITHDRAWN 작성자가 탈퇴", example = "NORMAL")
    private PostStatus status;

    /** 본인이 삭제한 일시 (DELETED일 때만) */
    @Schema(description = "삭제일시", hidden = true)
    private LocalDateTime deletedAt;

    /** 작성일 */
    @Schema(description = "작성일시", example = "2026-10-26T11:00:00")
    private LocalDateTime createdAt;

    /** 수정일 */
    @Schema(description = "수정일시", example = "2026-10-26T11:00:00")
    private LocalDateTime updatedAt;

    /** [users joined] 작성자 이름 */
    @Schema(description = "작성자 이름", example = "미쿠")
    private String authorName;

    /** [thumbnail_image joined] 작성자 프로필 이미지 주소 (/uploads/ + file_path, 없으면 응답에서 빠진다) */
    @Schema(description = "작성자 프로필 이미지 주소 (/uploads/ + file_path, 없으면 응답에서 빠진다)", example = "/uploads/1/thumbnail-image/user/2026/10/abc.png")
    private String authorThumbnailUrl;

    /** [users joined] 답글 대상 사용자 이름 (대상이 탈퇴했으면 빠진다) */
    @Schema(description = "답글 대상 사용자 이름. 대상이 탈퇴했으면 빠진다", example = "미쿠")
    private String mentionUserName;

    /** [request] 첨부할 댓글 이미지 id 목록 (최대 2개, 배열 순서가 표시 순서). 수정에서는 수정 후 최종 목록 */
    @Schema(description = "첨부할 댓글 이미지 id 0~2개 (요청에서만 쓴다). 배열 순서가 표시 순서. 수정할 때는 수정 후 최종 목록이고, 생략하면 이미지를 모두 지운다")
    @Size(max = 2)
    private List<Long> postCommentImageIds;

    /** [response] 내가 쓴 댓글인지 */
    @Schema(description = "내가 쓴 댓글인지", example = "false")
    private Boolean mine;

    /** [response] 첨부 이미지 (표시 순서대로, 없으면 빈 배열) */
    @Schema(description = "첨부 이미지 (표시 순서대로, 없으면 빈 배열)")
    private List<PostCommentImageVO> images;

    /** [response] 답글 목록 (최상위 댓글에서만 채워지고 답글에서는 빈 배열) */
    @Schema(description = "답글 목록 (오래된 순). 답글 자신은 항상 빈 배열이다")
    private List<PostCommentVO> replies;
}
