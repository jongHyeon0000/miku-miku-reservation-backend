package com.jong.mikumikureservationbackend.common.scheduler;

import com.jong.mikumikureservationbackend.service.post.PostCommentImageCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/* 매일 00시 10분에 고아 댓글 이미지를 정리한다(에디터 이미지 정리와 겹치지 않게 10분 뒤) */
@Component
@RequiredArgsConstructor
public class PostCommentImageCleanupScheduler {

    private final PostCommentImageCleanupService postCommentImageCleanupService;

    @Scheduled(cron = "0 10 0 * * *")
    public void cleanup() {
        postCommentImageCleanupService.cleanup();
    }
}
