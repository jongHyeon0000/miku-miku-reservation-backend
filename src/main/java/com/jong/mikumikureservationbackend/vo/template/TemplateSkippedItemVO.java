package com.jong.mikumikureservationbackend.vo.template;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 매퍼 조회 결과: 내 분류에 이미 있어서 제외될 항목 한 건 (제조사는 상위가 없어 groupName이 null) */
@Getter
@Setter
@NoArgsConstructor
public class TemplateSkippedItemVO {

    private String groupName;
    private String name;
}
