INSERT INTO median_incomes (year, household_size, earn_percent, monthly_income) VALUES
(2026, 1, 60, 154),
(2026, 1, 80, 205),
(2026, 1, 100, 256),
(2026, 1, 120, 308),
(2026, 1, 150, 385),
(2026, 1, 180, 462),
(2026, 2, 60, 252),
(2026, 2, 80, 336),
(2026, 2, 100, 420),
(2026, 2, 120, 504),
(2026, 2, 150, 630),
(2026, 2, 180, 756),
(2026, 3, 60, 322),
(2026, 3, 80, 429),
(2026, 3, 100, 536),
(2026, 3, 120, 643),
(2026, 3, 150, 804),
(2026, 3, 180, 965),
(2026, 4, 60, 390),
(2026, 4, 80, 520),
(2026, 4, 100, 649),
(2026, 4, 120, 779),
(2026, 4, 150, 974),
(2026, 4, 180, 1169),
(2026, 5, 60, 453),
(2026, 5, 80, 605),
(2026, 5, 100, 756),
(2026, 5, 120, 907),
(2026, 5, 150, 1134),
(2026, 5, 180, 1360),
(2026, 6, 60, 513),
(2026, 6, 80, 684),
(2026, 6, 100, 856),
(2026, 6, 120, 1027),
(2026, 6, 150, 1283),
(2026, 6, 180, 1540);

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
          E'**제1조 (목적)**\n본 약관은 **''Y-Fin.''**(이하 ''Y-Fin.'')이 제공하는 금융 정보 시뮬레이션 및 자격 자가진단 관련 서비스의 이용 조건 및 절차, 운영 주체와 회원 간의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다.\n\n**제2조 (용어의 정의)**\n\n1. ''서비스''라 함은 **Y-Fin.**이 구현하여 회원이 이용할 수 있는 금융상품 적합도 시뮬레이션, 금융 정보 큐레이션 및 관련 부가 서비스를 의미합니다.\n2. ''회원''이라 함은 **Y-Fin.**의 서비스에 접속하여 본 약관에 동의하고 서비스를 이용하는 고객을 말합니다.\n\n**제3조 (약관의 효력 및 변경)**\n\n1. 본 약관은 서비스를 이용하고자 하는 모든 회원에 대하여 그 효력을 발생합니다.\n2. **Y-Fin.**은 관계 법령을 위배하지 않는 범위에서 본 약관을 개정할 수 있으며, 변경된 약관은 서비스 내 공지사항을 통해 공지함으로써 효력이 발생합니다.\n\n**제4조 (서비스의 내용 및 성격)**\n\n1. **Y-Fin.**은 회원이 입력한 정보를 바탕으로 금융상품 가입 요건과의 부합 여부를 계산하는 시뮬레이션 기능을 제공합니다.\n2. **Y-Fin.**은 공공데이터 및 금융기관의 공개 정보를 기반으로 한 상품 정보를 안내하며, 상세 확인을 위한 해당 기관 공식 홈페이지로의 연결(아웃링크)을 제공합니다.\n3. **본 서비스는 금융소비자보호법상의 ''금융상품판매대리·중개업''에 해당하지 않습니다. Y-Fin.은 금융상품 계약 체결권을 가지지 않으며, 단지 정보 제공 및 자격 자가진단 도구만을 제공합니다.**\n\n**제5조 (면책 및 책임의 제한)**\n\n1. **데이터 정확성: Y-Fin.은 회원이 수동으로 입력한 데이터의 정확성을 전제로 결과를 산출하며, 이용자가 입력한 정보의 오류, 허위 기재 또는 누락으로 인해 발생한 결과에 대하여 Y-Fin.은 어떠한 법적 책임도 지지 않습니다.**\n2. **결과의 비보장: Y-Fin.이 제공하는 적합도 점수 및 매칭률은 단순 참고용 시뮬레이션 결과입니다. 이는 실제 금융기관의 대출 승인, 최종 확정 이율, 가입 가능 여부를 결정하거나 법적으로 보장하지 않습니다.**\n3. **계약 책임: Y-Fin.은 금융상품의 계약 당사자가 아니며, 회원이 개별 금융기관과 체결하는 계약 내용, 서비스 품질 및 그로 인해 발생하는 분쟁에 대하여 개입하거나 책임지지 않습니다.**\n\n**제6조 (이용자의 의무)**\n회원은 서비스 이용 시 타인의 정보를 도용하거나 허위 사실을 기재해서는 안 되며, 서비스의 정상적인 운영을 방해하는 행위를 해서는 안 됩니다.\n\n**제7조 (준거법 및 재판관할)**\n본 약관과 관련하여 발생한 분쟁에 대하여는 대한민국 법을 준거법으로 하며, **Y-Fin.** 운영 주체 소재지의 관할 법원을 합의 관할 법원으로 합니다.',
          TRUE,
          '2026-03-12T00:00:00+09:00'
      ),
      (
          (SELECT id FROM terms WHERE code = 'PRIVACY_POLICY'),
          1, 0,
          '[필수] 개인정보 수집·이용 동의 전문',
          E'''**Y-Fin.**''은 「개인정보 보호법」 등 관련 법령에 따라 사용자의 개인정보를 보호하며, 서비스 이용을 위해 아래와 같이 개인정보를 수집·이용합니다.\n\n**1. 개인정보의 수집 및 이용 목적**\n\n- 서비스 이용에 따른 본인 식별 및 회원 관리\n- **사용자 맞춤형 금융상품 적합도 시뮬레이션 결과 산출 및 데이터 분석**\n- 서비스 개선을 위한 통계 분석 및 고객 문의 응대\n\n**2. 수집하는 개인정보의 항목**\n\n- **계정 정보:** 이메일 주소, 비밀번호, 닉네임\n- **시뮬레이션 정보:** 생년월일, 거주지역, 직종, 직전년도 소득 수준, 병역 여부(군필/복무중/미필), 주거 상태(무주택 여부 등)\n\n| **수집 항목** | **수집 및 이용 목적** | **보유 및 이용 기간** |\n| --- | --- | --- |\n| 이메일, 비밀번호, 닉네임 | 회원 가입 및 식별, 서비스 이용 | **서비스 탈퇴 시까지** (단, 법령에 따른 보존 필요 시 해당 기간까지) |\n| 생년월일, 거주지, 직종, 소득, 병역 여부, 주거 상태 | **맞춤형 금융상품 시뮬레이션 결과 산출**, 사용자 적합도 분석 및 정보 제공 | 서비스 이용 기간 동안 보유 |\n\n**3. 개인정보의 보유 및 이용 기간**\n\n- **회원 탈퇴 시 즉시 파기하는 것을 원칙으로 합니다.**\n- 단, 관련 법령에 따라 보존 의무가 있는 경우 해당 법령이 정한 기간 동안 안전하게 보관합니다.\n\n**4. 제3자 제공에 관한 사항**\n\n- **Y-Fin.은 원칙적으로 회원의 개인정보를 외부에 제공하지 않습니다.** 단, 회원이 아웃링크를 통해 금융기관 페이지로 이동하여 직접 정보를 입력하는 행위는 **Y-Fin.**의 데이터 제공 범위에 포함되지 않습니다.\n\n**5. 동의 거부권 및 불이익 안내**\n\n- 귀하는 개인정보 수집 및 이용 동의를 거부할 권리가 있습니다. 단, 필수 항목 동의 거부 시 시뮬레이션 결과 확인 등 서비스 이용이 제한될 수 있습니다.\n\n**6. 개인정보 보호책임자**\n\n- 성명: **이서빈**\n- 연락처: [팀 공용 메일]',
          TRUE,
          '2026-03-12T00:00:00+09:00'
      ),
      (
          (SELECT id FROM terms WHERE code = 'LOCATION_SERVICE_TERMS'),
          1, 0,
          '[선택] 위치기반 서비스 이용약관 전문',
          E'**제1조 (목적)**\n본 약관은 회원이 **Y-Fin.**이 제공하는 위치기반 서비스를 이용함에 있어 **Y-Fin.**과 회원 사이의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다.\n\n**제2조 (서비스의 내용)**\n**Y-Fin.**은 사용자의 현재 위치 정보를 활용하여 다음과 같은 서비스를 제공합니다.\n\n1. 사용자 인근 금융기관 지점 정보 및 위치 안내\n2. **사용자의 현재 위치 또는 거주지 기반 지역 특화 정책 금융상품 정보 큐레이션**\n\n**제3조 (회원의 권리)**\n회원은 언제든지 위치기반 서비스 이용에 대한 동의의 전부 또는 일부를 철회할 수 있습니다.\n\n**제4조 (위치정보의 보존 및 파기)**\n\n1. **Y-Fin.**은 위치정보법에 따라 위치정보 이용·제공사실 확인자료를 시스템에 자동으로 기록하며, 해당 자료를 **6개월 이상 보관**합니다.\n2. 이용 목적 달성 시 재생이 불가능한 방법으로 즉시 파기합니다.',
          TRUE,
          '2026-03-12T00:00:00+09:00'
      ),
      (
          (SELECT id FROM terms WHERE code = 'MARKETING_TERMS'),
          1, 0,
          '[선택] 마케팅 정보 수신 및 프로파일링 동의 전문',
          E'**1. 수집 및 이용 목적**\n''**Y-Fin.**''은 사용자가 입력한 금융 관련 프로필(나이, 소득, 지역 등) 및 서비스 이용 기록을 분석(**프로파일링**)하여 귀하에게 최적화된 청년 정책 금융 상품 안내 및 개인화된 금융 혜택 정보를 제공하고자 합니다.\n\n**2. 수집 및 분석 항목**\n\n- 서비스 내 입력된 프로필 정보, 상품 클릭 로그, 시뮬레이션 수행 이력\n\n**3. 수신 채널 및 방법**\n\n- 앱 푸시(Push) 알림, 알림톡(카카오톡), SMS, 이메일 중 이용자가 허용한 채널을 통해 발송합니다.\n\n**4. 동의 철회 및 불이익 안내**\n\n- **본 동의는 선택 사항이며, 거부하더라도 기본 시뮬레이션 서비스 이용에는 제한이 없습니다.**\n- 동의 후에도 마이페이지 설정을 통해 언제든지 수신 거부 및 동의 철회가 가능합니다.',
          TRUE,
          '2026-03-12T00:00:00+09:00'
      );

INSERT INTO category (name) VALUES
('거주지역'),
('현재신분'),
('저축기간'),
('혜택선택'),
('상품관심사'),
('우대거래');

INSERT INTO category_option (category_id, value, code) VALUES
(1, '서울', 'REGION_SEOUL'),
(1, '부산', 'REGION_BUSAN'),
(1, '대구', 'REGION_DAEGU'),
(1, '인천', 'REGION_INCHEON'),
(1, '광주', 'REGION_GWANGJU'),
(1, '대전', 'REGION_DAEJEON'),
(1, '울산', 'REGION_ULSAN'),
(1, '세종', 'REGION_SEJONG'),
(1, '경기', 'REGION_GYEONGGI'),
(1, '강원', 'REGION_GANGWON'),
(1, '충북', 'REGION_CHUNGBUK'),
(1, '충남', 'REGION_CHUNGNAM'),
(1, '전북', 'REGION_JEONBUK'),
(1, '전남', 'REGION_JEONNAM'),
(1, '경북', 'REGION_GYEONGBUK'),
(1, '경남', 'REGION_GYEONGNAM'),
(1, '제주', 'REGION_JEJU'),
(2, '미취업자', 'STATUS_UNEMPLOYED'),
(2, '아르바이트/프리랜서', 'STATUS_PART_TIME'),
(2, '중소기업 재직', 'STATUS_SME_WORKER'),
(2, '군복무', 'STATUS_MILITARY'),
(3, '3년 이상', 'TERM_OVER_3_YEARS'),
(3, '2~3년', 'TERM_2_TO_3_YEARS'),
(3, '1년 내외', 'TERM_AROUND_1_YEAR'),
(4, '최고이율 중시', 'BENEFIT_MAX_INTEREST'),
(4, '비과세', 'BENEFIT_TAX_FREE'),
(4, '우대조건 간편', 'BENEFIT_EASY_CONDITION'),
(4, '정부기여금', 'BENEFIT_GOV_SUBSIDY'),
(4, '내집마련', 'BENEFIT_HOUSE_PREPARE'),
(5, '저축', 'INTEREST_SAVINGS'),
(5, '대출', 'INTEREST_LOAN'),
(6, '첫거래 고객', 'BANK_FIRST_TRANSACTION'),
(6, '급여이체 가능', 'BANK_SALARY_TRANSFER'),
(6, '카드실적 연동', 'BANK_CARD_USAGE'),
(6, '자동이체 가능', 'BANK_AUTO_TRANSFER'),
(6, '마케팅 동의', 'BANK_MARKETING'),
(6, '재예치', 'BANK_REDEPOSIT')
;

INSERT INTO provider (source_id, code, name) VALUES
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'MOHW', '보건복지부'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'KINFA', '서민금융진흥원'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'BUSAN_CITY', '부산광역시'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'GYEONGGI', '경기도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'INCHEON', '인천광역시'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'GANGWON', '강원특별자치도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'GWANGJU', '광주광역시'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'JEONNAM', '전라남도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'GYEONGNAM', '경상남도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'JEONBUK', '전북특별자치도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'DAEGU', '대구광역시'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'SEJONG', '세종특별자치시'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'GYEONGBUK', '경상북도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'HAMAN', '함안군');

INSERT INTO product (source_id, type, product_code, product_name, content) VALUES
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY001', '청년내일저축계좌', '매칭 유형: 정액 | 본인 부담금: 월 10만~50만 원 본인 저축 | 매칭 기여금: 정부 근로소득장려금 중위소득 50% 이하 월 30만 원 정액(1:3) | 정부기여금 환산수익률: 연 100.0% (3년, 본인 월10만 기준) | 비고: 전국 단일사업. 2026년부터 차상위 초과자 신규모집 중단'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY002', '청년 자산형성 지원 (청년미래적금)', '매칭 유형: 정률 (월 납입 비례, 만기 누적) | 본인 부담금: 월 최대 50만 원 자유 납입, 3년 | 매칭 기여금: 정부기여금 일반형 108만 원 / 우대형 216만 원 | 정부기여금 환산수익률: 일반형 연 2.0% / 우대형 연 4.0% | 비고: 서민금융진흥원. 2026.6 출시 예정 (청년도약계좌 대체). 기본 5% + 기관별 우대 2~3%'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY003', '부산청년 기쁨두배통장', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원, 24/36개월 | 매칭 기여금: 부산시 1:1 동일액 | 정부기여금 환산수익률: 연 50.0%(2년) / 33.3%(3년) | 비고: 부산시·부산은행 협약 (boogi2.kr)'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY004', '청년 노동자 통장 (경기도)', '매칭 유형: 정률 1:1 + 지역화폐 | 본인 부담금: 월 10만 원, 24개월 | 매칭 기여금: 경기도 1:1(현금 240만) + 지역화폐 100만 | 정부기여금 환산수익률: 연 50.0%(현금) / 70.8%(지역화폐 포함) | 비고: 경기도미래세대재단 운영'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY005', '드림For 청년통장 (인천시)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 15만 원 x 36회 = 540만 원 | 매칭 기여금: 인천시 540만 원(3년 만기 일괄) | 정부기여금 환산수익률: 연 33.3% (3년) | 비고: 인천시. 취급: 신한 청년DREAM적금'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY006', '청년발달장애인 자산형성지원 (행복씨앗통장, 인천)', '매칭 유형: 정액 | 본인 부담금: 월 15만 원 x 36개월 = 540만 원 | 매칭 기여금: 지원금 월 15만 원 정액(1:1) | 정부기여금 환산수익률: 연 33.3% (3년) | 비고: 인천시, 청년 발달장애인 대상'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY007', '청년 재가 중증장애인 자산형성 지원 (강원도)', '매칭 유형: 정액 | 본인 부담금: 월 15만 원 이상, 3년 | 매칭 기여금: 지원금 월 15만 원 정액(1:1) | 정부기여금 환산수익률: 연 33.3% (3년) | 비고: 강원도, 재가 중증장애 청년 대상'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY008', '광주 청년13(일+삶)통장', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 10개월 = 100만 원 | 매칭 기여금: 광주시 100만 원(만기 일괄) | 정부기여금 환산수익률: 연 120.0% (10개월) | 비고: 광주청년통합플랫폼 운영 (단기 10개월)'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY009', '청년 희망디딤돌 통장 (전남)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 36개월 = 360만 원 | 매칭 기여금: 전남도(40%)+시군(60%) 동일액(1:1) | 정부기여금 환산수익률: 연 33.3% (3년) | 비고: 전남. 도·시군 운영분 통합(구 9·19번)'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY010', '모다드림 청년통장 (경남)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 20만 원 x 24개월 = 480만 원 | 매칭 기여금: 경남도·시군 480만 원(1:1) | 정부기여금 환산수익률: 연 50.0% (2년) | 비고: 경남. BNK경남은행. 도·시군 운영분 통합(구 10·20번)'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY011', '청년 디딤돌 2배 적금 (강원도)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 36개월 = 360만 원 | 매칭 기여금: 기업 5만+도·시 5만 = 월 10만(1:1) | 정부기여금 환산수익률: 연 33.3% (3년) | 비고: 강원특별자치도 경제진흥원'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY012', '전북청년 함께 두배적금 (전북)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 24개월 = 240만 원 | 매칭 기여금: 도·시군 240만 원(1:1) | 정부기여금 환산수익률: 연 50.0% (2년) | 비고: 전북. 14개 시군 명의 통장'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY013', '대구 청년희망적금', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 12개월 = 120만 원 | 매칭 기여금: 대구시 120만 원(1:1) | 정부기여금 환산수익률: 연 100.0% (1년) | 비고: 대구시. 8개월 이상 근로 조건 (단기 1년)'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY014', '세종 청년미래적금 (청년희망적금)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 15만 원 x 36개월 = 540만 원 | 매칭 기여금: 세종시 540만 원(1:1, 만기 일시지급) | 정부기여금 환산수익률: 연 33.3% (3년) | 비고: 세종시. 금융위 상품과 별개 지자체 사업'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY015', '청년 사랑채움사업 (경북 내 22개 시·군)', '매칭 유형: 정률 1:1 + 결혼축하금 | 본인 부담금: 월 20만 원 x 24회 = 480만 원 | 매칭 기여금: 지자체 480만 원(1:1) + 결혼축하금 120만(조건부) | 정부기여금 환산수익률: 연 50.0% (2년, 축하금 환산 제외) | 비고: 경북도경제진흥원 통합사업(구 15·16·17번)'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'POLICY016', '함안정착 청년통장 (함안군)', '매칭 유형: 정률 1:1.5 | 본인 부담금: 월 20만 원 x 36개월 = 720만 원 | 매칭 기여금: 함안군 월 30만 x 36개월 = 1,080만 원(1.5배) | 정부기여금 환산수익률: 연 50.0% (3년) | 비고: 경남 함안군. 2026년 청년·군 3년 적립으로 개편');

INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit,
    min_age, max_age, allows_military_age_extension, military_max_age,
    earn_max_amt, earn_percent, min_tenure_months, requires_homeless, requires_householder, is_joinable,
    apply_url, intr_rate_type, save_trm
) VALUES
((SELECT id FROM product WHERE product_code = 'POLICY001'), (SELECT id FROM provider WHERE code = 'MOHW'), NULL, NULL, 100.00, 'FIXED_AMOUNT', NULL, 300000, 36, false, 100000, 500000, 15, 39, false, NULL, NULL, 50, NULL, false, false, true, 'https://www.bokjiro.go.kr/ssis-tbu/ssis-tbu/twataa/wlfareInfo/moveTWAT52011M.do?wlfareInfoId=WLF00000060', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY002'), (SELECT id FROM provider WHERE code = 'KINFA'), NULL, NULL, 2.00, 'RATIO', 0.0600, NULL, 36, false, 1000, 500000, 19, 34, true, 40, 60000000, 200, NULL, false, false, true, 'https://ylaccount.kinfa.or.kr/main', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY002'), (SELECT id FROM provider WHERE code = 'KINFA'), NULL, NULL, 4.00, 'RATIO', 0.1200, NULL, 36, false, 1000, 500000, 19, 34, true, 40, 36000000, 150, NULL, false, false, true, 'https://ylaccount.kinfa.or.kr/main', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY003'), (SELECT id FROM provider WHERE code = 'BUSAN_CITY'), NULL, NULL, 50.00, 'RATIO', 1.0000, NULL, 24, false, 100000, 100000, 18, 39, false, NULL, 43068000, NULL, NULL, false, false, true, 'https://young.busan.go.kr/index.nm?menuCd=53', NULL, 24),
((SELECT id FROM product WHERE product_code = 'POLICY003'), (SELECT id FROM provider WHERE code = 'BUSAN_CITY'), NULL, NULL, 33.33, 'RATIO', 1.0000, NULL, 36, false, 100000, 100000, 18, 39, false, NULL, 43068000, NULL, NULL, false, false, true, 'https://young.busan.go.kr/index.nm?menuCd=53', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY004'), (SELECT id FROM provider WHERE code = 'GYEONGGI'), NULL, NULL, 70.83, 'RATIO', 1.4167, NULL, 24, false, 100000, 100000, 19, 39, false, NULL, NULL, 120, NULL, false, false, true, 'https://account.ggwf.or.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'POLICY005'), (SELECT id FROM provider WHERE code = 'INCHEON'), NULL, NULL, 33.33, 'RATIO', 1.0000, NULL, 36, false, 150000, 150000, 18, 39, true, 42, 40000000, NULL, NULL, false, false, true, 'https://youth.incheon.go.kr/youthpolicy/youthPolicyInfoDetail.do?poly_seq=418', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY006'), (SELECT id FROM provider WHERE code = 'INCHEON'), NULL, NULL, 33.33, 'FIXED_AMOUNT', NULL, 150000, 36, false, 150000, 150000, 16, 39, false, NULL, NULL, 100, NULL, false, false, true, 'https://www.incheon.go.kr/IC010101/view?nttNo=2043592&curPage=1', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY007'), (SELECT id FROM provider WHERE code = 'GANGWON'), NULL, NULL, 33.33, 'FIXED_AMOUNT', NULL, 150000, 36, false, 150000, NULL, 15, 39, false, NULL, NULL, 100, NULL, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY008'), (SELECT id FROM provider WHERE code = 'GWANGJU'), NULL, NULL, 120.00, 'RATIO', 1.0000, NULL, 10, false, 100000, 100000, 19, 39, false, NULL, 36960000, NULL, 3, false, false, true, 'https://youth.gwangju.go.kr/www/', NULL, 10),
((SELECT id FROM product WHERE product_code = 'POLICY009'), (SELECT id FROM provider WHERE code = 'JEONNAM'), NULL, NULL, 33.33, 'RATIO', 1.0000, NULL, 36, false, 100000, 100000, 18, 45, false, NULL, NULL, 120, 3, false, false, true, 'https://www.jeonnam.go.kr/J0203/boardView.do?seq=23937&infoReturn=&menuId=jeonnam0203000000&displayHeader=&searchType=&searchText=&searchStDate=&searchEnDate=&pageIndex=1&boardId=J0203&displayHeader=', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY010'), (SELECT id FROM provider WHERE code = 'GYEONGNAM'), NULL, NULL, 50.00, 'RATIO', 1.0000, NULL, 24, false, 200000, 200000, 18, 39, false, NULL, NULL, 130, 3, false, false, true, 'https://www.modadream.kr/', NULL, 24),
((SELECT id FROM product WHERE product_code = 'POLICY011'), (SELECT id FROM provider WHERE code = 'GANGWON'), NULL, NULL, 33.33, 'RATIO', 1.0000, NULL, 36, false, 100000, 100000, 18, 45, false, NULL, NULL, 150, NULL, false, false, true, 'https://double.gwwell.kr/youth', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY012'), (SELECT id FROM provider WHERE code = 'JEONBUK'), NULL, NULL, 50.00, 'RATIO', 1.0000, NULL, 24, false, 100000, 100000, 18, 39, false, NULL, NULL, 140, 5, false, false, true, 'https://double.jb2030.or.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'POLICY013'), (SELECT id FROM provider WHERE code = 'DAEGU'), NULL, NULL, 100.00, 'RATIO', 1.0000, NULL, 12, false, 100000, 100000, 19, 39, false, NULL, NULL, 120, 8, false, false, true, 'https://youthdream.daegu.go.kr', NULL, 12),
((SELECT id FROM product WHERE product_code = 'POLICY014'), (SELECT id FROM provider WHERE code = 'SEJONG'), NULL, NULL, 33.33, 'RATIO', 1.0000, NULL, 36, false, 150000, 150000, 19, 39, false, NULL, NULL, 120, 6, false, false, true, 'https://sjyouth.sjepa.or.kr/', NULL, 36),
((SELECT id FROM product WHERE product_code = 'POLICY015'), (SELECT id FROM provider WHERE code = 'GYEONGBUK'), NULL, NULL, 50.00, 'RATIO', 1.0000, NULL, 24, false, 200000, 200000, 19, 39, false, NULL, NULL, 150, 3, false, false, true, 'https://gbwork.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'POLICY016'), (SELECT id FROM provider WHERE code = 'HAMAN'), NULL, NULL, 50.00, 'RATIO', 1.5000, NULL, 36, false, 200000, 200000, 19, 49, false, NULL, NULL, NULL, NULL, false, false, true, 'https://www.haman.go.kr', NULL, 36);

INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'ONTONG') AND p.type = 'POLICY'
UNION ALL SELECT pp.id, 'REGION_BUSAN' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY003'
UNION ALL SELECT pp.id, 'REGION_GYEONGGI' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY004'
UNION ALL SELECT pp.id, 'REGION_INCHEON' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('POLICY005', 'POLICY006')
UNION ALL SELECT pp.id, 'REGION_GANGWON' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('POLICY007', 'POLICY011')
UNION ALL SELECT pp.id, 'REGION_GWANGJU' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY008'
UNION ALL SELECT pp.id, 'REGION_JEONNAM' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY009'
UNION ALL SELECT pp.id, 'REGION_GYEONGNAM' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('POLICY010', 'POLICY016')
UNION ALL SELECT pp.id, 'REGION_JEONBUK' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY012'
UNION ALL SELECT pp.id, 'REGION_DAEGU' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY013'
UNION ALL SELECT pp.id, 'REGION_SEJONG' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY014'
UNION ALL SELECT pp.id, 'REGION_GYEONGBUK' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY015'
UNION ALL SELECT pp.id, 'STATUS_SME_WORKER' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('POLICY011', 'POLICY015', 'POLICY016')
UNION ALL SELECT pp.id, 'STATUS_SME_WORKER' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'POLICY002' AND pp.gov_matching_ratio = 0.1200;
