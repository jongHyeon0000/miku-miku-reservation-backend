-- 로컬 개발용 테스트 데이터 삭제 (MariaDB)
-- 수동으로 적용한다: mysql -uroot --default-character-set=utf8mb4 miku_miku_reservation < sample-data-delete.sql
-- sample-data-insert.sql이 넣은 행을 이름으로 찾아 지운다(대상 계정 @u). 다른 데이터는 지우지 않는다.
--  * 상품은 이름으로 지운다(연결 행은 ON DELETE CASCADE로 함께 지워진다).
--  * 하위 분류는 (상위 이름, 하위 이름)으로 지운다. 다른 상품이 쓰고 있으면 FK(RESTRICT)에 막혀 오류로 멈춘다.
--  * 상위 분류는 하위가 하나도 남아 있지 않을 때만 지운다(직접 만든 하위가 같이 지워지지 않게).
--  * 같은 이름으로 직접 만든 행이 있으면 같이 지워질 수 있으니, 실제 데이터를 넣은 뒤에는 실행하지 않는다.
--
-- 썸네일 이미지는 DB 행만 지운다(정상 삭제는 파일도 지우지만 SQL은 파일을 못 지운다).
-- 업로드 폴더의 파일은 필요하면 직접 지운다:
--   U=~/dev-tools/dev-resource/miku-miku-reservation/uploads/1/thumbnail-image/figure/2026/10
--   rm -f "$U/785b2abd-57b8-4647-a3d9-efb4d278334d.jpg" "$U/ed1db194-6c1c-49eb-ab74-5ff4d0e67aee.jpg"

SET NAMES utf8mb4;
SET @u = 1;

-- 상품이 분류를 참조(RESTRICT)하므로 상품을 가장 먼저 지운다. 썸네일은 대상에 FK가 없어서 먼저 지운다.
DELETE FROM thumbnail_image
WHERE user_id = @u AND target_type = 'FIGURE' AND file_path IN (
  '1/thumbnail-image/figure/2026/10/785b2abd-57b8-4647-a3d9-efb4d278334d.jpg',
  '1/thumbnail-image/figure/2026/10/ed1db194-6c1c-49eb-ab74-5ff4d0e67aee.jpg');

DELETE FROM figure WHERE user_id = @u AND name IN (
  '하츠네 미쿠 매지컬미라이 2025 Ver. 1/7 스케일 피규어',
  '넨도로이드 푸치 하쿠레이 레이무');

-- 하위 분류
DELETE s FROM shop s JOIN shop_group g ON g.shop_group_id = s.shop_group_id
WHERE s.user_id = @u AND ((g.name = '네이버 스토어' AND s.name = '굿스마일 PW') OR (g.name = '일반 스토어' AND s.name = '코믹스아트'));

DELETE t FROM figure_type t JOIN figure_type_group g ON g.figure_type_group_id = t.figure_type_group_id
WHERE t.user_id = @u AND ((g.name = '스케일' AND t.name = '1/7 스케일') OR (g.name = '넨도로이드' AND t.name = '푸치'));

DELETE s FROM series s JOIN series_group g ON g.series_group_id = s.series_group_id
WHERE s.user_id = @u AND ((g.name = '굿스마일 라인업' AND s.name IN ('팝업퍼레이드', '매지컬 미라이', '레이싱 미쿠 시리즈')) OR (g.name = '후모 시리즈' AND s.name = '후모 인형'));

DELETE c FROM genre_character c JOIN genre_character_group g ON g.genre_character_group_id = c.genre_character_group_id
WHERE c.user_id = @u AND ((g.name = '보컬로이드' AND c.name = '하츠네 미쿠') OR (g.name = '동방프로젝트' AND c.name = '하쿠레이 레이무'));

-- 상위 분류(하위가 남아 있지 않을 때만)
DELETE FROM shop_group
WHERE user_id = @u AND name IN ('네이버 스토어', '일반 스토어')
  AND NOT EXISTS (SELECT 1 FROM shop s WHERE s.shop_group_id = shop_group.shop_group_id);

DELETE FROM figure_type_group
WHERE user_id = @u AND name IN ('스케일', '넨도로이드')
  AND NOT EXISTS (SELECT 1 FROM figure_type t WHERE t.figure_type_group_id = figure_type_group.figure_type_group_id);

DELETE FROM series_group
WHERE user_id = @u AND name IN ('굿스마일 라인업', '후모 시리즈')
  AND NOT EXISTS (SELECT 1 FROM series s WHERE s.series_group_id = series_group.series_group_id);

DELETE FROM genre_character_group
WHERE user_id = @u AND name IN ('보컬로이드', '동방프로젝트')
  AND NOT EXISTS (SELECT 1 FROM genre_character c WHERE c.genre_character_group_id = genre_character_group.genre_character_group_id);

-- 제조사
DELETE FROM maker WHERE user_id = @u AND name IN ('후류', '굿스마일컴퍼니');
