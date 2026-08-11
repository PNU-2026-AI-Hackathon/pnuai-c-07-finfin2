DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

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
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    birthdate DATE,
    annual_income BIGINT,
    household_size INT,
    household_income_percent INT,
    tenure_months INT,
    is_first_job BOOLEAN,
    is_homeless BOOLEAN,
    is_householder BOOLEAN,
    monthly_savings_goal BIGINT,
    main_banks TEXT, -- 롤백을 위해 보존한 미사용 컬럼
    never_used_banks TEXT,
    matured_saving_banks TEXT,
    selected_option_ids TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_profiles_user UNIQUE (user_id)
);

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

CREATE TABLE term_versions (
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL,
    major_version INTEGER NOT NULL,
    minor_version INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_term_version_term FOREIGN KEY (term_id) REFERENCES terms(id) ON DELETE CASCADE,
    CONSTRAINT uq_term_versions_version UNIQUE (term_id, major_version, minor_version)
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
    CONSTRAINT fk_user_term_agreement_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_term_agreements_version FOREIGN KEY (term_version_id) REFERENCES term_versions(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_term_agreements_user_version UNIQUE (user_id, term_version_id),
    CONSTRAINT chk_user_term_agreements_agreed_at CHECK (
        (agreed = TRUE AND agreed_at IS NOT NULL) OR (agreed = FALSE)
    )
);

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE category_option (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES category(id),
    value VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL
);

CREATE TABLE product_source (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);

INSERT INTO product_source (code, name) VALUES
('FSS', '금융감독원'),
('ONTONG', '온통청년');

CREATE TABLE provider (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES product_source(id),
    code VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    apply_url VARCHAR(500)
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES product_source(id),
    type VARCHAR(20) NOT NULL,
    product_code VARCHAR(100),
    product_name VARCHAR(200) NOT NULL,
    content TEXT,
    content_summary TEXT,
    join_method TEXT,
    eligibility_text TEXT,
    caution_text TEXT,
    recruitment_period TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_properties (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    provider_id BIGINT NOT NULL REFERENCES provider(id),
    base_rate DECIMAL(5,2),
    max_rate DECIMAL(5,2),
    gov_contribution_rate DECIMAL(5,2),
    gov_contribution_type VARCHAR(30),
    gov_matching_ratio DECIMAL(8,4),
    gov_monthly_fixed_contribution BIGINT,
    gov_contribution_period_months INT,
    exclude_from_rate_comparison BOOLEAN NOT NULL DEFAULT FALSE,
    min_monthly_limit BIGINT,
    max_monthly_limit BIGINT,
    min_age INT,
    max_age INT,
    allows_military_age_extension BOOLEAN NOT NULL DEFAULT FALSE,
    military_max_age INT,
    earn_max_amt BIGINT,
    earn_percent INT,
    min_tenure_months INT,
    requires_homeless BOOLEAN NOT NULL DEFAULT FALSE,
    requires_householder BOOLEAN NOT NULL DEFAULT FALSE,
    is_joinable BOOLEAN NOT NULL DEFAULT TRUE,
    apply_url VARCHAR(500),
    intr_rate_type VARCHAR(30),
    rsrv_type VARCHAR(30),
    save_trm INT,
    variant_code VARCHAR(50)
);

CREATE TABLE product_property_keyword (
    id BIGSERIAL PRIMARY KEY,
    product_property_id BIGINT NOT NULL REFERENCES product_properties(id) ON DELETE CASCADE,
    keyword_code VARCHAR(50) NOT NULL
);

CREATE TABLE product_property_required_keyword (
    id BIGSERIAL PRIMARY KEY,
    product_property_id BIGINT NOT NULL REFERENCES product_properties(id) ON DELETE CASCADE,
    keyword_code VARCHAR(50) NOT NULL,
    effect VARCHAR(20) NOT NULL DEFAULT 'REQUIRE',
    confidence VARCHAR(20) NOT NULL DEFAULT 'HIGH'
);

CREATE TABLE product_preferential_rates (
    id BIGSERIAL PRIMARY KEY,
    product_property_id BIGINT NOT NULL REFERENCES product_properties(id) ON DELETE CASCADE,
    keyword_code VARCHAR(50) NOT NULL,
    rate DECIMAL(5,2) NOT NULL,
    description TEXT,
    min_age INT,
    max_age INT
);

CREATE TABLE my_fin (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULl,
    product_property_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_my_fin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_my_fin_product_property FOREIGN KEY (product_property_id) REFERENCES product_properties(id) ON DELETE CASCADE,
    CONSTRAINT uq_my_fin_user_property UNIQUE (user_id,product_property_id)
);
