package com.jong.mikumikureservationbackend.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 아이디 찾기 응답 {loginId}
 */
@Schema(description = "아이디 찾기 응답")
@Getter
@RequiredArgsConstructor
public class FindIdResponse {

    /** 이메일로 가입된 계정의 로그인 아이디 */
    @Schema(description = "이메일로 가입된 계정의 로그인 아이디", example = "jong1234")
    private final String loginId;
}
