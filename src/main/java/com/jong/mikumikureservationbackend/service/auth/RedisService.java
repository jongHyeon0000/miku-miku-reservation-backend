package com.jong.mikumikureservationbackend.service.auth;

import java.time.Duration;

public interface RedisService {

    /* 문자열 값을 만료 시간과 함께 저장한다(같은 키는 덮어씀) */
    void set(String key, String value, Duration ttl);

    /* 값을 조회한다. 없거나 만료됐으면 null */
    String get(String key);

    /* 숫자 값을 1 올리고 올린 값을 돌려준다(키가 없으면 1부터 시작) */
    Long increment(String key);

    /* 키의 남은 만료 시간을 다시 정한다 */
    void expire(String key, Duration ttl);

    void delete(String key);

    boolean hasKey(String key);
}
