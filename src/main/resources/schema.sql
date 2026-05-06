DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

-- CREATE TYPE user_role AS ENUM (
--     'BEFORE_AGREED',
--     'BASIC_ACCESS',
--     'RECOMMENDATION',
--     'ADMIN'
--     );

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255),
                       name VARCHAR(100) NOT NULL,
                       provider VARCHAR(50) NOT NULL,
                       provider_id VARCHAR(255) NOT NULL,
                       user_role VARCHAR(50) NOT NULL DEFAULT 'BEFORE_AGREED',
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT uq_users_provider_account UNIQUE (provider, provider_id)
);

CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token_hash VARCHAR(255) NOT NULL,
                                is_active BOOLEAN NOT NULL,
                                expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE median_incomes (
    id BIGSERIAL PRIMARY KEY,
    year INT NOT NULL CHECK (year > 0),
    household_size INT NOT NULL CHECK (household_size > 0),
    earn_percent INT NOT NULL CHECK (earn_percent > 0),
    monthly_income INT NOT NULL CHECK (monthly_income >= 0),

    CONSTRAINT uq_year_household_size_earn_percent UNIQUE (year, household_size, earn_percent)
);

CREATE TABLE terms (
       id BIGSERIAL PRIMARY KEY,
       code VARCHAR(100) NOT NULL UNIQUE,
       is_required BOOLEAN NOT NULL,
       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE term_versions(
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL,
    major_version INTEGER NOT NULL,
    minor_version INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 효력이 생기는 날짜
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_term_version_term
                         FOREIGN KEY (term_id) REFERENCES terms(id) ON DELETE CASCADE,

    CONSTRAINT uq_term_versions_version
                         UNIQUE (term_id, major_version, minor_version)
);

CREATE UNIQUE INDEX uq_term_version_current_per_term
    ON term_versions(term_id) WHERE is_current = TRUE;


CREATE TABLE user_term_agreements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    term_version_id BIGINT NOT NULL,
    agreed BOOLEAN NOT NULL,
    agreed_at TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_term_agreement_user
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_user_term_agreements_version
          FOREIGN KEY (term_version_id) REFERENCES term_versions(id) ON DELETE CASCADE,

    CONSTRAINT uq_user_term_agreements_user_version
          UNIQUE (user_id, term_version_id),

    CONSTRAINT chk_user_term_agreements_agreed_at
              CHECK (
                  (agreed = TRUE AND agreed_at IS NOT NULL) OR (agreed = FALSE)
              )
);

-- 약관 내용 삽입
-- 1. 약관 마스터 데이터 삽입 (terms)

CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 키워드에 따른 value 테이블
CREATE TABLE IF NOT EXISTS category_option (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    value VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
);


-- 키워드 카테고리 삽입

-- API 데이터 가공 후 저장할 테이블
-- 출처 테이블
CREATE TABLE product_source (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20)  NOT NULL UNIQUE,  -- 'FSS' | 'ONTONG'
    name    VARCHAR(50)  NOT NULL  -- '금융감독원' | '온통청년'
);

INSERT INTO product_source (code, name) VALUES
    ('FSS',    '금융감독원'),
    ('ONTONG', '온통청년');

-- 제공기관 테이블
CREATE TABLE provider (
    id        BIGSERIAL PRIMARY KEY,
    source_id BIGINT      NOT NULL REFERENCES product_source(id),
    code      VARCHAR(100), -- FSS: fin_co_no / ONTONG: sprvsnInstCd
    name      VARCHAR(100) NOT NULL  -- FSS: kor_co_nm / ONTONG: sprvsnInstCdNm 칼럼 값 가져와서 저장.
);

-- 상품 메인 테이블
CREATE TABLE product (
    id                   BIGSERIAL PRIMARY KEY,
    source_id            BIGINT       NOT NULL REFERENCES product_source(id),
    provider_id          BIGINT       NOT NULL REFERENCES provider(id),
    type                 VARCHAR(20)  NOT NULL,   -- 'DEPOSIT' | 'SAVING' | 'POLICY'
    product_code         VARCHAR(100),
    product_name         VARCHAR(200) NOT NULL,
    content              TEXT,  -- 상품 설명

    -- 공통
    base_rate            DECIMAL(5,2),
    max_rate             DECIMAL(5,2),
    min_monthly_limit    BIGINT,
    max_monthly_limit    BIGINT,

    -- 조건(파싱 결과)
    min_age              INT,
    max_age              INT,
    earn_max_amt         BIGINT, -- 최대 연 소득 (만원)
    earn_percent         INT, -- 중위소득 기준 % (ex : 150)
    min_tenure_months    INT, -- 최소 근속 기간 (개월)
    requires_homeless    BOOLEAN DEFAULT FALSE, -- 무주택 요건 여부
    requires_householder BOOLEAN DEFAULT FALSE, -- 세대주 요건 여부

    -- 현재 활성화 된(가입 가능한)상품 여부
    is_joinable         BOOLEAN NOT NULL DEFAULT True,
    -- 현재 온통청년에서만 url이 제공되고 있음.
    apply_url            VARCHAR(500),

    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 상품-옵션 테이블 (FSS 단리/복리, 기간)
CREATE TABLE product_option (
    id                BIGSERIAL PRIMARY KEY,
    product_id        BIGINT      NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    intr_rate_type    VARCHAR(10) NOT NULL,  -- 'S'(단리) | 'M'(복리)
    intr_rate_type_nm VARCHAR(20), -- '단리' | '복리'
    save_trm          INT  NOT NULL,  -- 저축 기간 (개월)
    intr_rate         DECIMAL(5,2), -- 기본 금리
    intr_rate2        DECIMAL(5,2) -- 최고 금리
);

-- 상품-키워드 테이블
CREATE TABLE product_keyword (
    id           BIGSERIAL PRIMARY KEY,
    product_id   BIGINT      NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    keyword_code VARCHAR(50) NOT NULL  -- KeywordValueEnum code값
);
