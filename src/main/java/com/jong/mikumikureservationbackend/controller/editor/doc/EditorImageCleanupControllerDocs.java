package com.jong.mikumikureservationbackend.controller.editor.doc;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageCleanupResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/* EditorImageCleanupController의 Swagger 설명 전용 인터페이스. 로직은 EditorImageCleanupController에만 둔다 */
@Tag(name = "개발용", description = "local 프로파일에서만 열리는 개발 보조 API. 권한 검증이 없다.")
public interface EditorImageCleanupControllerDocs {

    @Operation(
            summary = "고아 에디터 이미지 정리 수동 실행 (예: 어떤 상품에도 안 쓰이는 이미지 지우기)",
            description = "응답: scannedCount: 정리 대상 수, deletedCount: 삭제 수, failedCount: 실패 수")
    ApiResponse<EditorImageCleanupResponseVO> cleanup();
}
