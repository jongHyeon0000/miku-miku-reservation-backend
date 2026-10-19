package com.jong.mikumikureservationbackend.service.auth;

import com.jong.mikumikureservationbackend.vo.user.FindIdResponse;
import com.jong.mikumikureservationbackend.vo.user.LoginRequestVO;
import com.jong.mikumikureservationbackend.vo.user.LoginResponse;
import com.jong.mikumikureservationbackend.vo.user.UserVO;

public interface AuthService {

    /* 가입 시 분류 9개의 기본 '기타'도 만든다 */
    UserVO signup(UserVO request);

    /* 가입용 인증 코드를 메일로 보낸다. 이미 가입된 이메일이면 거절한다 */
    void sendSignupEmailCode(String email);

    /* 아이디 찾기용 인증 코드를 메일로 보낸다. 가입되지 않은 이메일이면 거절한다 */
    void sendFindIdEmailCode(String email);

    /* 이메일 인증을 마친 이메일의 계정 아이디를 돌려준다. 인증 완료는 1회용으로 소모된다 */
    FindIdResponse findId(String email);

    LoginResponse login(LoginRequestVO request);

    UserVO getMe(Long userId);

    /* 업로드 파일은 DB 삭제가 커밋된 뒤에 지운다 */
    void withdraw(Long userId, String password);
}
