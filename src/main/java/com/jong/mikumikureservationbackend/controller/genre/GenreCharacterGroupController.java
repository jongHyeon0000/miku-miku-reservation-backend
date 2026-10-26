package com.jong.mikumikureservationbackend.controller.genre;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.genre.doc.GenreCharacterGroupControllerDocs;
import com.jong.mikumikureservationbackend.service.genre.GenreCharacterGroupService;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterGroupVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/genre-character-group")
@RequiredArgsConstructor
public class GenreCharacterGroupController implements GenreCharacterGroupControllerDocs {

    private final GenreCharacterGroupService genreCharacterGroupService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<List<GenreCharacterGroupVO>> getTree() {
        return ApiResponse.ok("조회가 완료되었습니다.", genreCharacterGroupService.getTree(currentUser.getId()));
    }

    @Override
    @PostMapping
    public ApiResponse<GenreCharacterGroupVO> create(@Valid @RequestBody GenreCharacterGroupVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", genreCharacterGroupService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{genreCharacterGroupId}")
    public ApiResponse<Void> update(
            @PathVariable Long genreCharacterGroupId,
            @Valid @RequestBody GenreCharacterGroupVO request) {
        genreCharacterGroupService.update(currentUser.getId(), genreCharacterGroupId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{genreCharacterGroupId}")
    public ApiResponse<Void> delete(
            @PathVariable Long genreCharacterGroupId) {
        genreCharacterGroupService.delete(currentUser.getId(), genreCharacterGroupId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{genreCharacterGroupId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long genreCharacterGroupId) {
        return ApiResponse.ok("조회가 완료되었습니다.", genreCharacterGroupService.getUsage(currentUser.getId(), genreCharacterGroupId));
    }
}
