package com.jong.mikumikureservationbackend.controller.user.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.post.PostCommentVO;
import com.jong.mikumikureservationbackend.vo.post.PostVO;
import com.jong.mikumikureservationbackend.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/* UserController의 Swagger 설명 전용 인터페이스. 로직은 UserController에만 둔다 */
@Tag(name = "사용자 활동", description = "타인 프로필 정보와, 마이페이지와 타인 프로필에 몇 개만 띄우는 최근 글, 최근 답변. userId에 내 id(GET /api/auth/me의 userId)를 넣으면 내 것이고 다른 사용자의 id를 넣으면 그 사용자의 것이다. 로그인한 사용자만 조회할 수 있다. 전체 목록은 이 API가 아니라 커뮤니티에서 본다.")
public interface UserControllerDocs {

    @Operation(
            summary = "사용자 프로필 조회 (예: 글 작성자를 눌러 타인 프로필 보기)",
            description = "경로: userId: 조회할 사용자 id (글 목록, 글 상세의 userId, 댓글의 userId)\n\n"
                    + "응답: userId, name, biography(없으면 생략), thumbnailUrl(없으면 생략), postCount, commentCount, createdAt. "
                    + "아이디(loginId), 이메일은 나오지 않는다(내 것은 GET /api/auth/me). postCount, commentCount는 내 정보 조회와 같은 기준이다(정상 글, 정상 글에 단 정상 댓글과 답글). "
                    + "로그인한 사용자면 누구의 프로필이든 조회할 수 있다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "userId, name, biography, thumbnailUrl, postCount, commentCount, createdAt"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없거나 탈퇴한 사용자")
    })
    ApiResponse<UserVO> getProfile(
            @Parameter(description = "조회할 사용자 id", example = "1") Long userId);

    @Operation(
            summary = "사용자의 최근 글 조회 (예: 마이페이지 최근 글 3개)",
            description = "경로: userId: 조회할 사용자 id\n\n"
                    + "쿼리: size: 가져올 개수 (기본 5, 1 이상)\n\n"
                    + "응답: 배열. 항목은 postId, title, authorName, authorThumbnailUrl(없으면 생략), viewCount, commentCount, createdAt, status, mine이고 커뮤니티 목록(GET /api/post)의 항목에서 userId만 뺀 모양이다(경로의 userId와 같으므로). "
                    + "그 사용자의 정상 글만 최신순이다(삭제, 탈퇴한 글 제외, 본문 없음). 없거나 탈퇴한 사용자, 글이 없는 사용자는 빈 배열이다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "최근 글 배열 (없으면 빈 배열)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "size가 1 미만")
    })
    ApiResponse<List<PostVO>> getRecentPosts(
            @Parameter(description = "조회할 사용자 id", example = "1") Long userId,
            @Parameter(description = "가져올 개수 (기본 5)", example = "3") int size);

    @Operation(
            summary = "사용자의 최근 답변 조회 (예: 마이페이지 최근 답변 3개)",
            description = "경로: userId: 조회할 사용자 id\n\n"
                    + "쿼리: size: 가져올 개수 (기본 5, 1 이상)\n\n"
                    + "응답: 배열. 항목은 postCommentId, content, createdAt, postId, postTitle이고 postId, postTitle로 해당 글로 이동한다. "
                    + "그 사용자의 정상 댓글, 답글 중 달린 글도 정상인 것만 최신순이다(삭제, 탈퇴한 댓글과 삭제, 탈퇴한 글에 단 것 제외). 없거나 탈퇴한 사용자, 답변이 없는 사용자는 빈 배열이다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "최근 답변 배열 (없으면 빈 배열)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "size가 1 미만")
    })
    ApiResponse<List<PostCommentVO>> getRecentComments(
            @Parameter(description = "조회할 사용자 id", example = "1") Long userId,
            @Parameter(description = "가져올 개수 (기본 5)", example = "3") int size);
}
