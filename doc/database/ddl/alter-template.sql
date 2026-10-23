-- 분류 템플릿 추가 (수동 적용: 로컬 DB, 운영 DB 각각)
-- 템플릿 테이블 10개 신설, thumbnail_image 대상 종류에 템플릿 10종 추가
-- 템플릿에는 소유 계정이 없다(전부 공식). 이미지는 thumbnail_image를 그대로 쓴다.

-- ---------------------------------------------------------------- 템플릿 (이름표)
CREATE TABLE template (
  template_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_type VARCHAR(30)   NOT NULL COMMENT '템플릿 종류: SHOP 판매처, FIGURE_TYPE 형태, SERIES 시리즈, GENRE_CHARACTER 장르/캐릭터, MAKER 제조사',
  name          VARCHAR(100)  NOT NULL COMMENT '템플릿 이름 (동방프로젝트, 기본 제조사 등)',
  description   VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_id),
  UNIQUE KEY uk_template_type_name (template_type, name),
  CONSTRAINT ck_template_type CHECK (template_type IN ('SHOP','FIGURE_TYPE','SERIES','GENRE_CHARACTER','MAKER'))
) ENGINE=InnoDB COMMENT='분류 템플릿';

-- ---------------------------------------------------------------- 판매처 템플릿
CREATE TABLE template_shop_group (
  template_shop_group_id BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_id            BIGINT        NOT NULL COMMENT '소속 템플릿',
  name                   VARCHAR(100)  NOT NULL COMMENT '채널명',
  description            VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_shop_group_id),
  UNIQUE KEY uk_template_shop_group_name (template_id, name),
  CONSTRAINT fk_template_shop_group_template FOREIGN KEY (template_id) REFERENCES template (template_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='판매처 템플릿 상위(채널)';

CREATE TABLE template_shop (
  template_shop_id       BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_shop_group_id BIGINT        NOT NULL COMMENT '상위 채널',
  name                   VARCHAR(100)  NOT NULL COMMENT '스토어명',
  description            VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  url                    VARCHAR(2000) NULL COMMENT '판매처 바로가기 주소',
  created_at             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_shop_id),
  UNIQUE KEY uk_template_shop_group_name (template_shop_group_id, name),
  CONSTRAINT fk_template_shop_template_shop_group FOREIGN KEY (template_shop_group_id) REFERENCES template_shop_group (template_shop_group_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='판매처 템플릿 하위(스토어)';

-- ---------------------------------------------------------------- 형태 템플릿
CREATE TABLE template_figure_type_group (
  template_figure_type_group_id BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_id                   BIGINT        NOT NULL COMMENT '소속 템플릿',
  name                          VARCHAR(100)  NOT NULL COMMENT '형태 상위 이름',
  description                   VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at                    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at                    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_figure_type_group_id),
  UNIQUE KEY uk_template_figure_type_group_name (template_id, name),
  CONSTRAINT fk_template_figure_type_group_template FOREIGN KEY (template_id) REFERENCES template (template_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='형태 템플릿 상위';

CREATE TABLE template_figure_type (
  template_figure_type_id       BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_figure_type_group_id BIGINT        NOT NULL COMMENT '상위 형태',
  name                          VARCHAR(100)  NOT NULL COMMENT '형태 하위 이름',
  description                   VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at                    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at                    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_figure_type_id),
  UNIQUE KEY uk_template_figure_type_group_name (template_figure_type_group_id, name),
  CONSTRAINT fk_template_figure_type_template_figure_type_group FOREIGN KEY (template_figure_type_group_id) REFERENCES template_figure_type_group (template_figure_type_group_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='형태 템플릿 하위';

-- ---------------------------------------------------------------- 시리즈 템플릿
CREATE TABLE template_series_group (
  template_series_group_id BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_id              BIGINT        NOT NULL COMMENT '소속 템플릿',
  name                     VARCHAR(100)  NOT NULL COMMENT '시리즈 상위 이름',
  description              VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_series_group_id),
  UNIQUE KEY uk_template_series_group_name (template_id, name),
  CONSTRAINT fk_template_series_group_template FOREIGN KEY (template_id) REFERENCES template (template_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='시리즈 템플릿 상위';

CREATE TABLE template_series (
  template_series_id       BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_series_group_id BIGINT        NOT NULL COMMENT '상위 시리즈',
  name                     VARCHAR(100)  NOT NULL COMMENT '시리즈 하위 이름',
  description              VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_series_id),
  UNIQUE KEY uk_template_series_group_name (template_series_group_id, name),
  CONSTRAINT fk_template_series_template_series_group FOREIGN KEY (template_series_group_id) REFERENCES template_series_group (template_series_group_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='시리즈 템플릿 하위';

-- ---------------------------------------------------------------- 장르/캐릭터 템플릿
CREATE TABLE template_genre_character_group (
  template_genre_character_group_id BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_id                       BIGINT        NOT NULL COMMENT '소속 템플릿',
  name                              VARCHAR(100)  NOT NULL COMMENT '장르 이름',
  description                       VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at                        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at                        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_genre_character_group_id),
  UNIQUE KEY uk_template_genre_character_group_name (template_id, name),
  CONSTRAINT fk_template_genre_character_group_template FOREIGN KEY (template_id) REFERENCES template (template_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='장르/캐릭터 템플릿 상위(장르)';

CREATE TABLE template_genre_character (
  template_genre_character_id       BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_genre_character_group_id BIGINT        NOT NULL COMMENT '상위 장르',
  name                              VARCHAR(100)  NOT NULL COMMENT '캐릭터 이름',
  description                       VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at                        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at                        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_genre_character_id),
  UNIQUE KEY uk_template_genre_character_group_name (template_genre_character_group_id, name),
  CONSTRAINT fk_template_genre_character_template_genre_character_group FOREIGN KEY (template_genre_character_group_id) REFERENCES template_genre_character_group (template_genre_character_group_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='장르/캐릭터 템플릿 하위(캐릭터)';

-- ---------------------------------------------------------------- 제조사 템플릿 (그룹 없음, 템플릿에 바로 붙음)
CREATE TABLE template_maker (
  template_maker_id BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  template_id       BIGINT        NOT NULL COMMENT '소속 템플릿',
  name              VARCHAR(100)  NOT NULL COMMENT '제조사명',
  description       VARCHAR(1000) NULL COMMENT '설명 (일반 텍스트)',
  created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  PRIMARY KEY (template_maker_id),
  UNIQUE KEY uk_template_maker_name (template_id, name),
  CONSTRAINT fk_template_maker_template FOREIGN KEY (template_id) REFERENCES template (template_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='제조사 템플릿';

-- ---------------------------------------------------------------- 썸네일 대상 종류에 템플릿 10종 추가
-- 공식 이미지 행은 개발자 계정(user_id) 소유로 넣고, 파일은 업로드 루트의 template/ 폴더에 둔다.
ALTER TABLE thumbnail_image
  MODIFY COLUMN target_type VARCHAR(30) NOT NULL COMMENT '대상 종류: FIGURE 상품, SHOP_GROUP 판매처 채널, SHOP 판매처 스토어, FIGURE_TYPE_GROUP 형태 상위, FIGURE_TYPE 형태 하위, SERIES_GROUP 시리즈 상위, SERIES 시리즈 하위, GENRE_CHARACTER_GROUP 장르, GENRE_CHARACTER 캐릭터, MAKER 제조사, USER 계정(프로필, target_id는 user_id), TEMPLATE 템플릿, TEMPLATE_SHOP_GROUP, TEMPLATE_SHOP, TEMPLATE_FIGURE_TYPE_GROUP, TEMPLATE_FIGURE_TYPE, TEMPLATE_SERIES_GROUP, TEMPLATE_SERIES, TEMPLATE_GENRE_CHARACTER_GROUP, TEMPLATE_GENRE_CHARACTER, TEMPLATE_MAKER (템플릿 항목, 공식 이미지)',
  DROP CONSTRAINT ck_thumbnail_image_target_type,
  ADD CONSTRAINT ck_thumbnail_image_target_type CHECK (target_type IN ('FIGURE','SHOP_GROUP','SHOP','FIGURE_TYPE_GROUP','FIGURE_TYPE','SERIES_GROUP','SERIES','GENRE_CHARACTER_GROUP','GENRE_CHARACTER','MAKER','USER','TEMPLATE','TEMPLATE_SHOP_GROUP','TEMPLATE_SHOP','TEMPLATE_FIGURE_TYPE_GROUP','TEMPLATE_FIGURE_TYPE','TEMPLATE_SERIES_GROUP','TEMPLATE_SERIES','TEMPLATE_GENRE_CHARACTER_GROUP','TEMPLATE_GENRE_CHARACTER','TEMPLATE_MAKER'));
