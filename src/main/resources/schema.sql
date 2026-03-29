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
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT uq_users_provider_account UNIQUE (provider, provider_id)
);

CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token_hash VARCHAR(255) NOT NULL,
                                is_active BOOLEAN NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE terms (
       id BIGSERIAL PRIMARY KEY,
       code VARCHAR(100) NOT NULL UNIQUE,
       is_required BOOLEAN NOT NULL,
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE term_versions(
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL,
    major_version INTEGER NOT NULL,
    minor_version INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 효력이 생기는 날짜
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,


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
    agreed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

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
INSERT INTO terms (code, is_required) VALUES
                                          ('SERVICE_TERMS', TRUE),
                                          ('PRIVACY_POLICY', TRUE),
                                          ('LOCATION_SERVICE_TERMS', FALSE),
                                          ('MARKETING_TERMS', FALSE);

-- 2. 약관 버전 데이터 삽입 (term_versions)
INSERT INTO term_versions (
    term_id, major_version, minor_version, title, content, is_current, effective_from
) VALUES
      (
          (SELECT id FROM terms WHERE code = 'SERVICE_TERMS'),
          1, 0,
          '[필수] Y-Fin. 서비스 이용약관 전문',
          $$**제1조 (목적)**
본 약관은 **'Y-Fin.'**(이하 'Y-Fin.')이 제공하는 금융 정보 시뮬레이션 및 자격 자가진단 관련 서비스의 이용 조건 및 절차, 운영 주체와 회원 간의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다.

**제2조 (용어의 정의)**

1. '서비스'라 함은 **Y-Fin.**이 구현하여 회원이 이용할 수 있는 금융상품 적합도 시뮬레이션, 금융 정보 큐레이션 및 관련 부가 서비스를 의미합니다.
2. '회원'이라 함은 **Y-Fin.**의 서비스에 접속하여 본 약관에 동의하고 서비스를 이용하는 고객을 말합니다.

**제3조 (약관의 효력 및 변경)**

1. 본 약관은 서비스를 이용하고자 하는 모든 회원에 대하여 그 효력을 발생합니다.
2. **Y-Fin.**은 관계 법령을 위배하지 않는 범위에서 본 약관을 개정할 수 있으며, 변경된 약관은 서비스 내 공지사항을 통해 공지함으로써 효력이 발생합니다.

**제4조 (서비스의 내용 및 성격)**

1. **Y-Fin.**은 회원이 입력한 정보를 바탕으로 금융상품 가입 요건과의 부합 여부를 계산하는 시뮬레이션 기능을 제공합니다.
2. **Y-Fin.**은 공공데이터 및 금융기관의 공개 정보를 기반으로 한 상품 정보를 안내하며, 상세 확인을 위한 해당 기관 공식 홈페이지로의 연결(아웃링크)을 제공합니다.
3. **본 서비스는 금융소비자보호법상의 '금융상품판매대리·중개업'에 해당하지 않습니다. Y-Fin.은 금융상품 계약 체결권을 가지지 않으며, 단지 정보 제공 및 자격 자가진단 도구만을 제공합니다.**

**제5조 (면책 및 책임의 제한)**

1. **데이터 정확성: Y-Fin.은 회원이 수동으로 입력한 데이터의 정확성을 전제로 결과를 산출하며, 이용자가 입력한 정보의 오류, 허위 기재 또는 누락으로 인해 발생한 결과에 대하여 Y-Fin.은 어떠한 법적 책임도 지지 않습니다.**
2. **결과의 비보장: Y-Fin.이 제공하는 적합도 점수 및 매칭률은 단순 참고용 시뮬레이션 결과입니다. 이는 실제 금융기관의 대출 승인, 최종 확정 이율, 가입 가능 여부를 결정하거나 법적으로 보장하지 않습니다.**
3. **계약 책임: Y-Fin.은 금융상품의 계약 당사자가 아니며, 회원이 개별 금융기관과 체결하는 계약 내용, 서비스 품질 및 그로 인해 발생하는 분쟁에 대하여 개입하거나 책임지지 않습니다.**

**제6조 (이용자의 의무)**
회원은 서비스 이용 시 타인의 정보를 도용하거나 허위 사실을 기재해서는 안 되며, 서비스의 정상적인 운영을 방해하는 행위를 해서는 안 됩니다.

**제7조 (준거법 및 재판관할)**
본 약관과 관련하여 발생한 분쟁에 대하여는 대한민국 법을 준거법으로 하며, **Y-Fin.** 운영 주체 소재지의 관할 법원을 합의 관할 법원으로 합니다.$$,
          TRUE,
          '2026-03-12 00:00:00'
      ),
      (
          (SELECT id FROM terms WHERE code = 'PRIVACY_POLICY'),
          1, 0,
          '[필수] 개인정보 수집·이용 동의 전문',
          $$'**Y-Fin.**'은 「개인정보 보호법」 등 관련 법령에 따라 사용자의 개인정보를 보호하며, 서비스 이용을 위해 아래와 같이 개인정보를 수집·이용합니다.

**1. 개인정보의 수집 및 이용 목적**

- 서비스 이용에 따른 본인 식별 및 회원 관리
- **사용자 맞춤형 금융상품 적합도 시뮬레이션 결과 산출 및 데이터 분석**
- 서비스 개선을 위한 통계 분석 및 고객 문의 응대

**2. 수집하는 개인정보의 항목**

- **계정 정보:** 이메일 주소, 비밀번호, 닉네임
- **시뮬레이션 정보:** 생년월일, 거주지역, 직종, 직전년도 소득 수준, 병역 여부(군필/복무중/미필), 주거 상태(무주택 여부 등)

| **수집 항목** | **수집 및 이용 목적** | **보유 및 이용 기간** |
| --- | --- | --- |
| 이메일, 비밀번호, 닉네임 | 회원 가입 및 식별, 서비스 이용 | **서비스 탈퇴 시까지** (단, 법령에 따른 보존 필요 시 해당 기간까지) |
| 생년월일, 거주지, 직종, 소득, 병역 여부, 주거 상태 | **맞춤형 금융상품 시뮬레이션 결과 산출**, 사용자 적합도 분석 및 정보 제공 | 서비스 이용 기간 동안 보유 |

**3. 개인정보의 보유 및 이용 기간**

- **회원 탈퇴 시 즉시 파기하는 것을 원칙으로 합니다.**
- 단, 관련 법령에 따라 보존 의무가 있는 경우 해당 법령이 정한 기간 동안 안전하게 보관합니다.

**4. 제3자 제공에 관한 사항**

- **Y-Fin.은 원칙적으로 회원의 개인정보를 외부에 제공하지 않습니다.** 단, 회원이 아웃링크를 통해 금융기관 페이지로 이동하여 직접 정보를 입력하는 행위는 **Y-Fin.**의 데이터 제공 범위에 포함되지 않습니다.

**5. 동의 거부권 및 불이익 안내**

- 귀하는 개인정보 수집 및 이용 동의를 거부할 권리가 있습니다. 단, 필수 항목 동의 거부 시 시뮬레이션 결과 확인 등 서비스 이용이 제한될 수 있습니다.

**6. 개인정보 보호책임자**

- 성명: **이서빈**
- 연락처: [팀 공용 메일]$$,
          TRUE,
          '2026-03-12 00:00:00'
      ),
      (
          (SELECT id FROM terms WHERE code = 'LOCATION_SERVICE_TERMS'),
          1, 0,
          '[선택] 위치기반 서비스 이용약관 전문',
          $$**제1조 (목적)**
본 약관은 회원이 **Y-Fin.**이 제공하는 위치기반 서비스를 이용함에 있어 **Y-Fin.**과 회원 사이의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다.

**제2조 (서비스의 내용)**
**Y-Fin.**은 사용자의 현재 위치 정보를 활용하여 다음과 같은 서비스를 제공합니다.

1. 사용자 인근 금융기관 지점 정보 및 위치 안내
2. **사용자의 현재 위치 또는 거주지 기반 지역 특화 정책 금융상품 정보 큐레이션**

**제3조 (회원의 권리)**
회원은 언제든지 위치기반 서비스 이용에 대한 동의의 전부 또는 일부를 철회할 수 있습니다.

**제4조 (위치정보의 보존 및 파기)**

1. **Y-Fin.**은 위치정보법에 따라 위치정보 이용·제공사실 확인자료를 시스템에 자동으로 기록하며, 해당 자료를 **6개월 이상 보관**합니다.
2. 이용 목적 달성 시 재생이 불가능한 방법으로 즉시 파기합니다.$$,
          TRUE,
          '2026-03-12 00:00:00'
      ),
      (
          (SELECT id FROM terms WHERE code = 'MARKETING_TERMS'),
          1, 0,
          '[선택] 마케팅 정보 수신 및 프로파일링 동의 전문',
          $$**1. 수집 및 이용 목적**
'**Y-Fin.**'은 사용자가 입력한 금융 관련 프로필(나이, 소득, 지역 등) 및 서비스 이용 기록을 분석(**프로파일링**)하여 귀하에게 최적화된 청년 정책 금융 상품 안내 및 개인화된 금융 혜택 정보를 제공하고자 합니다.

**2. 수집 및 분석 항목**

- 서비스 내 입력된 프로필 정보, 상품 클릭 로그, 시뮬레이션 수행 이력

**3. 수신 채널 및 방법**

- 앱 푸시(Push) 알림, 알림톡(카카오톡), SMS, 이메일 중 이용자가 허용한 채널을 통해 발송합니다.

**4. 동의 철회 및 불이익 안내**

- **본 동의는 선택 사항이며, 거부하더라도 기본 시뮬레이션 서비스 이용에는 제한이 없습니다.**
- 동의 후에도 마이페이지 설정을 통해 언제든지 수신 거부 및 동의 철회가 가능합니다.$$,
          TRUE,
          '2026-03-12 00:00:00'
      );