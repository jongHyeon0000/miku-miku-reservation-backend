package com.jong.mikumikureservationbackend.controller.genre;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.genre.doc.GenreCharacterControllerDocs;
import com.jong.mikumikureservationbackend.service.genre.GenreCharacterService;
import com.jong.mikumikureservationbackend.vo.common.PageResponseVO;
import com.jong.mikumikureservationbackend.vo.common.UsageResponseVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterDeleteRequestVO;
import com.jong.mikumikureservationbackend.vo.genre.GenreCharacterVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genre-character")
@RequiredArgsConstructor
public class GenreCharacterController implements GenreCharacterControllerDocs {

    private final GenreCharacterService genreCharacterService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<PageResponseVO<GenreCharacterVO>> getPage(
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("조회가 완료되었습니다.", genreCharacterService.getPage(currentUser.getId(), groupId, page, size));
    }

    @Override
    @PostMapping
    public ApiResponse<GenreCharacterVO> create(@Valid @RequestBody GenreCharacterVO request) {
        return ApiResponse.ok("저장이 완료되었습니다.", genreCharacterService.create(currentUser.getId(), request));
    }

    @Override
    @PutMapping("/{genreCharacterId}")
    public ApiResponse<Void> update(
            @PathVariable Long genreCharacterId,
            @Valid @RequestBody GenreCharacterVO request) {
        genreCharacterService.update(currentUser.getId(), genreCharacterId, request);

        return ApiResponse.ok("수정이 완료되었습니다.");
    }

    @Override
    @DeleteMapping("/{genreCharacterId}")
    public ApiResponse<Void> delete(
            @PathVariable Long genreCharacterId) {
        genreCharacterService.delete(currentUser.getId(), genreCharacterId);

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @DeleteMapping
    public ApiResponse<Void> deleteAll(@RequestBody GenreCharacterDeleteRequestVO request) {
        genreCharacterService.deleteAll(currentUser.getId(), request.getGenreCharacterIds());

        return ApiResponse.ok("삭제가 완료되었습니다.");
    }

    @Override
    @GetMapping("/{genreCharacterId}/usage")
    public ApiResponse<UsageResponseVO> getUsage(
            @PathVariable Long genreCharacterId) {
        return ApiResponse.ok("조회가 완료되었습니다.", genreCharacterService.getUsage(currentUser.getId(), genreCharacterId));
    }
}
