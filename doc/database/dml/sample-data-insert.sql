-- 로컬 개발용 테스트 데이터 입력 (MariaDB)
-- 수동으로 적용한다: mysql -uroot --default-character-set=utf8mb4 miku_miku_reservation < sample-data-insert.sql
-- 대상 계정은 아래 @u (기본 1 = root). 한 번만 실행한다(다시 실행하면 이름 중복 UNIQUE 오류).
-- 지울 때는 sample-data-delete.sql (여기서 넣은 이름과 같은 행을 이름으로 찾아 지운다).
-- 상품 가격, 날짜, 링크는 모두 가상 값이다(실제 구매 내역이 아님).
--
-- 썸네일 이미지: 정상 업로드(PUT /api/upload/thumbnail-image/FIGURE/{id})로 만들어진 것과 같은 상태로 넣는다.
--  * DB에는 업로드 폴더(app.upload.dir) 기준 상대 경로만 저장한다. 로컬 업로드 폴더: ~/dev-tools/dev-resource/miku-miku-reservation/uploads
--  * 이 SQL의 썸네일 행은 아래 파일과 한 쌍이다. 파일이 없으면 이미지가 깨진다(이 PC의 업로드 폴더에만 있고 저장소에는 없다).
--      1/thumbnail-image/figure/2026/10/785b2abd-57b8-4647-a3d9-efb4d278334d.jpg   (하츠네 미쿠 1/7 스케일 피규어)
--      1/thumbnail-image/figure/2026/10/ed1db194-6c1c-49eb-ab74-5ff4d0e67aee.jpg   (넨도로이드 푸치 하쿠레이 레이무)
--  * 원본: IMG_6313.JPG (JPEG 387x516, 50501 byte). 앱은 대상마다 파일을 따로 저장하므로 같은 사진을 두 파일로 복사해 둔다.
--  * 복사 명령(파일이 없을 때):
--      U=~/dev-tools/dev-resource/miku-miku-reservation/uploads/1/thumbnail-image/figure/2026/10; mkdir -p "$U"
--      cp <원본 이미지> "$U/785b2abd-57b8-4647-a3d9-efb4d278334d.jpg"; cp <원본 이미지> "$U/ed1db194-6c1c-49eb-ab74-5ff4d0e67aee.jpg"

SET NAMES utf8mb4;
SET @u = 1;

-- ---------------------------------------------------------------- 판매처 (채널 2, 스토어 2)
INSERT INTO shop_group (user_id, name, description) VALUES
  (@u, '네이버 스토어', '네이버 스마트스토어로 운영되는 판매처 채널입니다.\n굿스마일 파트너 스토어와 개인 판매처가 대부분 이 채널에 들어 있습니다.'),
  (@u, '일반 스토어', '자체 쇼핑몰처럼 네이버 밖에서 구매하는 판매처 채널입니다.\n결제 수단과 배송 방식이 판매처마다 달라 상품 설명을 따로 확인합니다.');

INSERT INTO shop (user_id, shop_group_id, name, description)
SELECT @u, shop_group_id, '굿스마일 PW',
       '굿스마일컴퍼니 상품을 예약 판매하는 네이버 스토어입니다.\n예약 마감일이 짧은 편이라 상품마다 마감일을 확인해야 합니다.'
FROM shop_group WHERE user_id = @u AND name = '네이버 스토어';

INSERT INTO shop (user_id, shop_group_id, name, description)
SELECT @u, shop_group_id, '코믹스아트',
       '수입 피규어와 굿즈를 판매하는 온라인 쇼핑몰입니다.\n해외 발매 상품은 입고가 늦어질 수 있어 배송 예정월을 여유 있게 봅니다.'
FROM shop_group WHERE user_id = @u AND name = '일반 스토어';

-- ---------------------------------------------------------------- 형태 (상위 2, 하위 2)
INSERT INTO figure_type_group (user_id, name, description) VALUES
  (@u, '스케일', '실제 크기를 일정한 비율로 줄여 만든 피규어입니다.\n1/7, 1/8 등 비율이 클수록 크기가 작아지고 가격도 낮아집니다.'),
  (@u, '넨도로이드', '굿스마일컴퍼니의 데포르메 가동 피규어 시리즈입니다.\n머리가 크고 표정 파츠를 바꿔 끼울 수 있는 것이 특징입니다.');

INSERT INTO figure_type (user_id, figure_type_group_id, name, description)
SELECT @u, figure_type_group_id, '1/7 스케일',
       '높이 약 23cm 안팎의 1/7 스케일 피규어입니다.\n조형과 도색이 정교하고 상품 가격대가 높은 편입니다.'
FROM figure_type_group WHERE user_id = @u AND name = '스케일';

INSERT INTO figure_type (user_id, figure_type_group_id, name, description)
SELECT @u, figure_type_group_id, '푸치',
       '넨도로이드 푸치는 높이 약 7cm의 소형 미니 피규어입니다.\n상자 안에 1개씩 들어 있어 여러 캐릭터를 모아 진열하기 좋습니다.'
FROM figure_type_group WHERE user_id = @u AND name = '넨도로이드';

-- ---------------------------------------------------------------- 시리즈 (상위 2, 하위 4: 매지컬 미라이, 레이싱 미쿠 시리즈는 쓰이는 상품 없음)
INSERT INTO series_group (user_id, name, description) VALUES
  (@u, '굿스마일 라인업', '굿스마일컴퍼니가 발매하는 시리즈를 모은 묶음입니다.\n팝업퍼레이드와 넨도로이드 등이 여기에 들어갑니다.'),
  (@u, '후모 시리즈', '동방프로젝트 캐릭터를 둥글고 부드러운 모양의 봉제 인형으로 만든 시리즈 묶음입니다.\n캐릭터마다 같은 표정의 인형이 크기별로 나옵니다.');

INSERT INTO series (user_id, series_group_id, name, description)
SELECT @u, series_group_id, '팝업퍼레이드',
       '굿스마일컴퍼니의 논스케일 피규어 시리즈입니다.\n높이 약 17cm 안팎이고 스케일 피규어보다 합리적인 가격으로 발매됩니다.'
FROM series_group WHERE user_id = @u AND name = '굿스마일 라인업';

INSERT INTO series (user_id, series_group_id, name, description)
SELECT @u, series_group_id, '매지컬 미라이',
       '매년 열리는 하츠네 미쿠 팬 이벤트와 그 콘셉트 일러스트를 활용한 시리즈입니다.\n연도마다 새 일러스트가 공개되어 같은 캐릭터도 버전이 다릅니다.'
FROM series_group WHERE user_id = @u AND name = '굿스마일 라인업';

INSERT INTO series (user_id, series_group_id, name, description)
SELECT @u, series_group_id, '레이싱 미쿠 시리즈',
       '모터스포츠 레이싱 퀸 콘셉트의 하츠네 미쿠 시리즈입니다.\n매년 새 레이싱 의상과 디자인으로 피규어와 넨도로이드가 발매됩니다.'
FROM series_group WHERE user_id = @u AND name = '굿스마일 라인업';

INSERT INTO series (user_id, series_group_id, name, description)
SELECT @u, series_group_id, '후모 인형',
       '동방프로젝트 캐릭터를 모티브로 한 후모 봉제 인형입니다.\n푹신한 촉감에 표정이 단순해서 책상이나 선반에 올려 두기 좋습니다.'
FROM series_group WHERE user_id = @u AND name = '후모 시리즈';

-- ---------------------------------------------------------------- 장르, 캐릭터 (장르 2, 캐릭터 2)
INSERT INTO genre_character_group (user_id, name, description) VALUES
  (@u, '보컬로이드', '보컬로이드 소프트웨어와 그 캐릭터를 묶은 장르입니다.\n하츠네 미쿠를 비롯해 카가미네 린과 렌, 메구리네 루카 등이 속합니다.'),
  (@u, '동방프로젝트', 'ZUN이 만든 동인 슈팅 게임 시리즈와 그 캐릭터를 묶은 장르입니다.\n2차 창작이 활발해서 굿즈와 피규어가 꾸준히 나옵니다.');

INSERT INTO genre_character (user_id, genre_character_group_id, name, description)
SELECT @u, genre_character_group_id, '하츠네 미쿠',
       '크립톤 퓨처 미디어에서 만든 보컬로이드 캐릭터입니다.\n청록색 트윈테일이 상징이고 매년 다양한 일러스트 버전으로 피규어가 발매됩니다.'
FROM genre_character_group WHERE user_id = @u AND name = '보컬로이드';

INSERT INTO genre_character (user_id, genre_character_group_id, name, description)
SELECT @u, genre_character_group_id, '하쿠레이 레이무',
       '동방프로젝트의 주인공인 신사의 무녀입니다.\n붉은색과 흰색의 무녀복에 큰 리본이 특징입니다.'
FROM genre_character_group WHERE user_id = @u AND name = '동방프로젝트';

-- ---------------------------------------------------------------- 제조사 (2)
INSERT INTO maker (user_id, name, description) VALUES
  (@u, '후류', '일본의 프라이즈 피규어 중심 제조사입니다.\n스케일 피규어도 비교적 합리적인 가격으로 꾸준히 발매합니다.'),
  (@u, '굿스마일컴퍼니', '넨도로이드와 팝업 퍼레이드, 스케일 피규어를 만드는 일본 제조사입니다.\n예약 판매가 중심이라 예약 마감일을 꼭 확인해야 합니다.');

-- ---------------------------------------------------------------- 상품 (2)
-- 1) 미구매(예약 마감 임박): 결제 방식은 아직 없다
INSERT INTO figure (user_id, status, name, delivery_month, reservation_deadline, price, quantity,
                    figure_type_id, shop_id, maker_id, purchase_link, description)
VALUES (
  @u, 'UNPURCHASED', '하츠네 미쿠 매지컬미라이 2025 Ver. 1/7 스케일 피규어', '2027-03-01', '2026-10-31', 169000, 1,
  (SELECT t.figure_type_id FROM figure_type t JOIN figure_type_group g ON g.figure_type_group_id = t.figure_type_group_id
     WHERE t.user_id = @u AND g.name = '스케일' AND t.name = '1/7 스케일'),
  (SELECT s.shop_id FROM shop s JOIN shop_group g ON g.shop_group_id = s.shop_group_id
     WHERE s.user_id = @u AND g.name = '일반 스토어' AND s.name = '코믹스아트'),
  (SELECT maker_id FROM maker WHERE user_id = @u AND name = '후류'),
  'https://www.comicsart.co.kr/goods/view?no=48213',
  '<p>후류에서 발매하는 하츠네 미쿠 1/7 스케일 피규어입니다. 매지컬미라이 콘셉트 일러스트를 입체로 옮긴 디자인입니다.</p><p>예약 마감은 10월 31일이고 발매는 2027년 3월 예정입니다. 전체 높이는 약 23cm이며 전용 베이스가 포함됩니다.</p>'
);

-- 2) 구매완료(전액 결제)
INSERT INTO figure (user_id, status, payment_type, name, delivery_month, reservation_deadline, price, quantity,
                    paid_at, figure_type_id, shop_id, maker_id, purchase_link, description)
VALUES (
  @u, 'PURCHASED', 'FULL', '넨도로이드 푸치 하쿠레이 레이무', '2026-12-01', '2026-09-30', 9500, 1,
  '2026-10-05',
  (SELECT t.figure_type_id FROM figure_type t JOIN figure_type_group g ON g.figure_type_group_id = t.figure_type_group_id
     WHERE t.user_id = @u AND g.name = '넨도로이드' AND t.name = '푸치'),
  (SELECT s.shop_id FROM shop s JOIN shop_group g ON g.shop_group_id = s.shop_group_id
     WHERE s.user_id = @u AND g.name = '네이버 스토어' AND s.name = '굿스마일 PW'),
  (SELECT maker_id FROM maker WHERE user_id = @u AND name = '굿스마일컴퍼니'),
  'https://smartstore.naver.com/gsc-pw/products/10482931567',
  '<p>넨도로이드 푸치 시리즈로 발매된 하쿠레이 레이무입니다. 상자 안에 소형 피규어 1개가 들어 있습니다.</p><p>10월 초에 전액 결제를 마쳤고 12월 배송 예정입니다.</p>'
);

-- ---------------------------------------------------------------- 상품-시리즈, 상품-캐릭터 연결
INSERT INTO figure_series_map (user_id, figure_id, series_id)
SELECT @u, f.figure_id, s.series_id
FROM figure f
JOIN series s ON s.user_id = f.user_id
JOIN series_group g ON g.series_group_id = s.series_group_id
WHERE f.user_id = @u AND f.name = '하츠네 미쿠 매지컬미라이 2025 Ver. 1/7 스케일 피규어'
  AND g.name = '굿스마일 라인업' AND s.name = '팝업퍼레이드';

INSERT INTO figure_series_map (user_id, figure_id, series_id)
SELECT @u, f.figure_id, s.series_id
FROM figure f
JOIN series s ON s.user_id = f.user_id
JOIN series_group g ON g.series_group_id = s.series_group_id
WHERE f.user_id = @u AND f.name = '넨도로이드 푸치 하쿠레이 레이무'
  AND g.name = '후모 시리즈' AND s.name = '후모 인형';

INSERT INTO figure_character_map (user_id, figure_id, genre_character_id)
SELECT @u, f.figure_id, c.genre_character_id
FROM figure f
JOIN genre_character c ON c.user_id = f.user_id
JOIN genre_character_group g ON g.genre_character_group_id = c.genre_character_group_id
WHERE f.user_id = @u AND f.name = '하츠네 미쿠 매지컬미라이 2025 Ver. 1/7 스케일 피규어'
  AND g.name = '보컬로이드' AND c.name = '하츠네 미쿠';

INSERT INTO figure_character_map (user_id, figure_id, genre_character_id)
SELECT @u, f.figure_id, c.genre_character_id
FROM figure f
JOIN genre_character c ON c.user_id = f.user_id
JOIN genre_character_group g ON g.genre_character_group_id = c.genre_character_group_id
WHERE f.user_id = @u AND f.name = '넨도로이드 푸치 하쿠레이 레이무'
  AND g.name = '동방프로젝트' AND c.name = '하쿠레이 레이무';

-- ---------------------------------------------------------------- 썸네일 이미지 (상품 2개, 파일은 파일 머리말 참고)
INSERT INTO thumbnail_image (user_id, target_type, target_id, file_path, original_name, file_size)
SELECT @u, 'FIGURE', figure_id, '1/thumbnail-image/figure/2026/10/785b2abd-57b8-4647-a3d9-efb4d278334d.jpg', 'IMG_6313.JPG', 50501
FROM figure WHERE user_id = @u AND name = '하츠네 미쿠 매지컬미라이 2025 Ver. 1/7 스케일 피규어';

INSERT INTO thumbnail_image (user_id, target_type, target_id, file_path, original_name, file_size)
SELECT @u, 'FIGURE', figure_id, '1/thumbnail-image/figure/2026/10/ed1db194-6c1c-49eb-ab74-5ff4d0e67aee.jpg', 'IMG_6313.JPG', 50501
FROM figure WHERE user_id = @u AND name = '넨도로이드 푸치 하쿠레이 레이무';
