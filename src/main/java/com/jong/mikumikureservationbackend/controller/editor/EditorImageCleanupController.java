package com.jong.mikumikureservationbackend.controller.editor;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.controller.editor.doc.EditorImageCleanupControllerDocs;
import com.jong.mikumikureservationbackend.service.editor.EditorImageCleanupService;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageCleanupResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* 개발용: local 프로파일에서만 뜬다. 권한검증 없음(관리자 개념이 생기면 그때 붙인다) */
@Profile("local")
@RestController
@RequestMapping("/api/dev/editor-image/cleanup")
@RequiredArgsConstructor
public class EditorImageCleanupController implements EditorImageCleanupControllerDocs {

    private final EditorImageCleanupService editorImageCleanupService;

    @Override
    @PostMapping
    public ApiResponse<EditorImageCleanupResponseVO> cleanup() {
        return ApiResponse.ok("정리가 완료되었습니다.", editorImageCleanupService.cleanup());
    }
}
