package com.jong.mikumikureservationbackend.controller.editor;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.editor.doc.EditorImageControllerDocs;
import com.jong.mikumikureservationbackend.service.editor.EditorImageService;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload/editor-image")
@RequiredArgsConstructor
public class EditorImageController implements EditorImageControllerDocs {

    private final EditorImageService editorImageService;
    private final CurrentUser currentUser;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EditorImageVO> upload(
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("이미지 업로드가 완료되었습니다.", editorImageService.upload(currentUser.getId(), file));
    }
}
