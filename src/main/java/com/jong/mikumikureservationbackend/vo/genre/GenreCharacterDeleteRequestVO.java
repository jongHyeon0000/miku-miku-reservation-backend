package com.jong.mikumikureservationbackend.vo.genre;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 캐릭터 일괄 삭제 요청 본문.
 * [request] 표시가 붙은 필드는 컬럼이 아니다.
 */
@Schema(description = "캐릭터 일괄 삭제 요청 본문")
@Getter
@Setter
@NoArgsConstructor
public class GenreCharacterDeleteRequestVO {

    /** [request] 삭제할 캐릭터 id 목록 */
    @Schema(description = "삭제할 캐릭터 id 목록. 1개 이상", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> genreCharacterIds;
}
