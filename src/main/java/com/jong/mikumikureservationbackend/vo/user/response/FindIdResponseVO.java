package com.jong.mikumikureservationbackend.vo.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 아이디 찾기 응답 {loginId}
 */
@Schema(description = "아이디 찾기 응답")
@Getter
@RequiredArgsConstructor
public class FindIdResponseVO {

    /** 이메일로 가입된 계정의 로그인 아이디 */
    @Schema(description = "이메일로 가입된 계정의 로그인 아이디", example = "user1234")
    private final String loginId;
}
