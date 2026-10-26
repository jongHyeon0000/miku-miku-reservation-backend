package com.jong.mikumikureservationbackend.controller.thumbnail;

import com.jong.mikumikureservationbackend.common.constant.ThumbnailTarget;
import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.thumbnail.doc.ThumbnailImageControllerDocs;
import com.jong.mikumikureservationbackend.service.thumbnail.ThumbnailImageService;
import com.jong.mikumikureservationbackend.vo.thumbnail.ThumbnailUrlResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload/thumbnail-image")
@RequiredArgsConstructor
public class ThumbnailImageController implements ThumbnailImageControllerDocs {

    private final ThumbnailImageService thumbnailImageService;
    private final CurrentUser currentUser;

    @Override
    @PutMapping(value = "/{targetType}/{targetId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ThumbnailUrlResponseVO> upload(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @RequestParam("file") MultipartFile file) {
        ThumbnailTarget target = ThumbnailTarget.fromUrlValue(targetType);

        return ApiResponse.ok("업로드가 완료되었습니다.", thumbnailImageService.upload(currentUser.getId(), target, targetId, file));
    }

    @Override
    @DeleteMapping("/{targetType}/{targetId}")
    public ApiResponse<Void> delete(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        thumbnailImageService.delete(currentUser.getId(), ThumbnailTarget.fromUrlValue(targetType), targetId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }
}
