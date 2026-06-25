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
((SELECT id FROM product_source WHERE code = 'FSS'), '0010363', '더케이저축은행'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'GOV001', '금융위원회'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'MOHW', '보건복지부'),
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
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'HAMAN', '함안군'),
((SELECT id FROM product_source WHERE code = 'FSS'), '0010364', '국민은행');

INSERT INTO product (source_id, type, product_code, product_name, content) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', '240076', 'e-쎄이프 정기예금', '단리/복리 선택 가능'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'SUBSCRIPTION', 'GOV002', '청년우대형 청약통장', '청년 자산형성 주거 상품'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV003', '청년내일저축계좌', '신청: 2026.5.4~5.20 | 나이: 만 15~39세 | 대상: 중위소득 50% 이하(차상위 이하) 근로·사업소득 청년 | 월 납입: 10만~50만 원 | 기간: 3년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV004', '청년 자산형성 지원 (청년미래적금)', '신청: 2026.6 출시 예정 (청년도약계좌 대체) | 나이: 만 19~34세(병역 최대 6년 차감) | 일반형: 총급여 6,000만 원 이하 또는 연매출 3억 원 이하 소상공인 + 가구중위 200% 이하 | 우대형: 총급여 3,600만 원 이하 중소기업 재직 또는 연매출 1억 원 이하 소상공인 + 가구중위 150% 이하 | 월 납입: 1천~50만 원 | 기간: 3년 만기 | 기본금리 5% + 기관별 우대 2~3%'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV005', '부산청년 기쁨두배통장', '신청: 2025.7.8~7.23(연1회 추첨, 6,000명) | 나이: 만 18~39세 | 대상: 부산 거주 근로청년, 월소득 358.9만 원 이하(세전), 4대보험 1개 이상 | 월 납입: 10만 원 | 기간: 2년/3년 선택'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV006', '청년 노동자 통장 (경기도)', '신청: 2025.8.1~8.18(2026년 공고 미정) | 나이: 만 19~39세 | 대상: 경기 거주 근로청년, 가구 중위 120% 이하 | 월 납입: 10만 원 | 기간: 2년 만기 | 지역화폐 100만 원 별도 지급'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV007', '드림For 청년통장 (인천시)', '신청: 2026.5.4~5.15 | 나이: 만 18~39세(제대군인 +3년) | 대상: 인천 거주 근로청년, 연소득 2,190만~4,000만 원, 4대보험·주35h 이상 | 월 납입: 15만 원 | 기간: 3년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV008', '청년발달장애인 자산형성지원 (행복씨앗통장, 인천)', '신청: 2026.1~12 | 나이: 만 16~39세 | 대상: 인천 거주 지적·자폐(발달)장애인, 중위 100% 이하 | 월 납입: 15만 원 | 기간: 3년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV009', '청년 재가 중증장애인 자산형성 지원 (강원도)', '신청: 강원도 모집공고 기준 | 나이: 만 15~40세 미만 | 대상: 강원 1년 이상 거주 중증장애 청년, 중위 100% 이하 | 월 납입: 15만 원 이상 | 기간: 3년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV010', '광주 청년13(일+삶)통장', '신청: 2026.1.8~1.18 | 나이: 만 19~39세 | 대상: 광주 거주 근로청년(3개월 이상 근로), 월급여 92만~308만 원 | 월 납입: 10만 원 | 기간: 10개월 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV011', '청년 희망디딤돌 통장 (전남)', '신청: 2026.4.20~5.8 | 나이: 만 18~45세 | 대상: 전남 거주 노동자/사업자(6개월 중 3개월 이상 근로), 중위 120% 이하 | 월 납입: 10만 원 | 기간: 3년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV012', '모다드림 청년통장 (경남)', '신청: 2026.1.19~2.12 | 나이: 만 18~39세 | 대상: 경남도 사업장 근로청년(3개월 이상 근로), 중위 130% 이하 | 월 납입: 20만 원 | 기간: 2년 | 취급: BNK경남은행'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV013', '청년 디딤돌 2배 적금 (강원도)', '신청: 2026.5.21~5.26 | 나이: 만 18~45세 | 대상: 강원 거주 중소기업 재직청년, 중위 150% 이하 | 월 납입: 10만 원 | 기간: 3년'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV014', '전북청년 함께 두배적금 (전북)', '신청: 2026.3.3~3.16 | 나이: 만 18~39세 | 대상: 전북 거주 청년(5개월 이상 근로, 주15h 이상), 중위 140% 이하 | 월 납입: 10만 원 | 기간: 2년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV015', '대구 청년희망적금', '신청: 2026.6~7(미정) | 나이: 만 19~39세 | 대상: 대구 거주 근로청년(8개월 이상 근로, 고용보험), 본인 중위 120% 이하·가구 140% 이하 | 월 납입: 10만 원 | 기간: 1년 만기'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV016', '세종 청년 희망적금', '신청: 2026.5.6~5.15 | 나이: 만 19~39세 | 대상: 세종 6개월 이상 거주 근로청년(동일사업장 6개월 이상, 주30h 이상), 중위 120% 이하 | 월 납입: 15만 원 | 기간: 3년'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV017', '청년 사랑채움사업 (경북 내 22개 시·군)', '신청: 2026.3.23~4.15 | 나이: 만 19~39세 | 대상: 경북 도내 중소·중견기업 3개월 이상 재직 미혼청년(주30h 이상), 중위 150% 이하 | 월 납입: 20만 원 | 기간: 2년 만기 | 결혼축하금 120만 원은 조건부 별도'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'GOV018', '함안정착 청년통장 (함안군)', '신청: 2026.5.8~5.22 | 나이: 만 19~49세 | 대상: 함안군 전입 거주(전 거주지 1년 이상)·지역 중소기업 근로청년 | 월 납입: 20만 원 | 기간: 3년 만기'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'BANK001', '청년우대적금', '만 19~29세 전용 우대 적금');

INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    min_age, max_age, earn_max_amt, earn_percent, min_tenure_months, requires_homeless, requires_householder, is_joinable,
    apply_url,
    intr_rate_type, save_trm
) VALUES
((SELECT id FROM product WHERE product_code = '240076'), (SELECT id FROM provider WHERE code = '0010363'), 3.45, 3.45, 10, 100, NULL, NULL, NULL, NULL, 19, 34, NULL, NULL, NULL, false, false, true, NULL, 'SINGLE_INTEREST', 12),
((SELECT id FROM product WHERE product_code = '240076'), (SELECT id FROM provider WHERE code = '0010363'), 3.45, 3.45, 10, 100, NULL, NULL, NULL, NULL, 19, 34, NULL, NULL, NULL, false, false, true, NULL, 'COMPOUND_INTEREST', 12),
((SELECT id FROM product WHERE product_code = 'GOV002'), (SELECT id FROM provider WHERE code = 'GOV001'), NULL, NULL, 1, 70, NULL, NULL, NULL, NULL, 19, 34, NULL, NULL, NULL, true, false, true, NULL, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'GOV003'), (SELECT id FROM provider WHERE code = 'MOHW'), NULL, NULL, 100000, 500000, 'FIXED_AMOUNT', NULL, 300000, 36, 15, 39, NULL, 50, NULL, false, false, true, 'https://www.bokjiro.go.kr/ssis-tbu/twataa/wlfareInfo/moveTWAT52011M.do?wlfareInfoId=WLF00000060', NULL, NULL),
((SELECT id FROM product WHERE product_code = 'GOV004'), (SELECT id FROM provider WHERE code = 'GOV001'), NULL, NULL, 1000, 500000, 'RATIO', 0.0600, NULL, 36, 19, 34, 60000000, 200, NULL, false, false, true, 'https://www.fsc.go.kr/no010101/86767?curPage=7&srchBeginDt=&srchCtgry=&srchEndDt=&srchKey=&srchText=', NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV004'), (SELECT id FROM provider WHERE code = 'GOV001'), NULL, NULL, 1000, 500000, 'RATIO', 0.1200, NULL, 36, 19, 34, 36000000, 150, NULL, false, false, true, 'https://www.fsc.go.kr/no010101/86767?curPage=7&srchBeginDt=&srchCtgry=&srchEndDt=&srchKey=&srchText=', NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV005'), (SELECT id FROM provider WHERE code = 'BUSAN_CITY'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 24, 18, 39, 43068000, NULL, NULL, false, false, true, NULL, NULL, 24),
((SELECT id FROM product WHERE product_code = 'GOV005'), (SELECT id FROM provider WHERE code = 'BUSAN_CITY'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 36, 18, 39, 43068000, NULL, NULL, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV006'), (SELECT id FROM provider WHERE code = 'GYEONGGI'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 24, 19, 39, NULL, 120, NULL, false, false, true, 'https://account.ggwf.or.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'GOV007'), (SELECT id FROM provider WHERE code = 'INCHEON'), NULL, NULL, 150000, 150000, 'RATIO', 1.0000, NULL, 36, 18, 39, 40000000, NULL, NULL, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV008'), (SELECT id FROM provider WHERE code = 'INCHEON'), NULL, NULL, 150000, 150000, 'FIXED_AMOUNT', NULL, 150000, 36, 16, 39, NULL, 100, NULL, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV009'), (SELECT id FROM provider WHERE code = 'GANGWON'), NULL, NULL, 150000, NULL, 'FIXED_AMOUNT', NULL, 150000, 36, 15, 39, NULL, 100, NULL, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV010'), (SELECT id FROM provider WHERE code = 'GWANGJU'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 10, 19, 39, 36960000, NULL, 3, false, false, true, NULL, NULL, 10),
((SELECT id FROM product WHERE product_code = 'GOV011'), (SELECT id FROM provider WHERE code = 'JEONNAM'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 36, 18, 45, NULL, 120, 3, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV012'), (SELECT id FROM provider WHERE code = 'GYEONGNAM'), NULL, NULL, 200000, 200000, 'RATIO', 1.0000, NULL, 24, 18, 39, NULL, 130, 3, false, false, true, 'https://www.modadream.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'GOV013'), (SELECT id FROM provider WHERE code = 'GANGWON'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 36, 18, 45, NULL, 150, NULL, false, false, true, 'https://double.gwwell.kr', NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV014'), (SELECT id FROM provider WHERE code = 'JEONBUK'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 24, 18, 39, NULL, 140, 5, false, false, true, 'https://double.jb2030.or.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'GOV015'), (SELECT id FROM provider WHERE code = 'DAEGU'), NULL, NULL, 100000, 100000, 'RATIO', 1.0000, NULL, 12, 19, 39, NULL, 120, 8, false, false, true, NULL, NULL, 12),
((SELECT id FROM product WHERE product_code = 'GOV016'), (SELECT id FROM provider WHERE code = 'SEJONG'), NULL, NULL, 150000, 150000, 'RATIO', 1.0000, NULL, 36, 19, 39, NULL, 120, 6, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'GOV017'), (SELECT id FROM provider WHERE code = 'GYEONGBUK'), NULL, NULL, 200000, 200000, 'RATIO', 1.0000, NULL, 24, 19, 39, NULL, 150, 3, false, false, true, 'https://gbwork.kr', NULL, 24),
((SELECT id FROM product WHERE product_code = 'GOV018'), (SELECT id FROM provider WHERE code = 'HAMAN'), NULL, NULL, 200000, 200000, 'RATIO', 1.5000, NULL, 36, 19, 49, NULL, NULL, NULL, false, false, true, NULL, NULL, 36),
((SELECT id FROM product WHERE product_code = 'BANK001'), (SELECT id FROM provider WHERE code = '0010364'), 3.8, 4.5, 10, 50, NULL, NULL, NULL, NULL, 19, 29, NULL, NULL, NULL, false, false, true, NULL, 'SINGLE_INTEREST', 12),
((SELECT id FROM product WHERE product_code = 'BANK001'), (SELECT id FROM provider WHERE code = '0010364'), 3.5, 4.2, 10, 50, NULL, NULL, NULL, NULL, 19, 29, NULL, NULL, NULL, false, false, true, NULL, 'SINGLE_INTEREST', 24),
((SELECT id FROM product WHERE product_code = 'BANK001'), (SELECT id FROM provider WHERE code = '0010364'), 3.8, 4.5, 10, 50, NULL, NULL, NULL, NULL, 19, 29, NULL, NULL, NULL, false, false, true, NULL, 'COMPOUND_INTEREST', 12);

INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'ONTONG') AND p.type = 'POLICY'
UNION ALL SELECT pp.id, 'REGION_BUSAN' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV005'
UNION ALL SELECT pp.id, 'REGION_GYEONGGI' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV006'
UNION ALL SELECT pp.id, 'REGION_INCHEON' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('GOV007', 'GOV008')
UNION ALL SELECT pp.id, 'REGION_GANGWON' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('GOV009', 'GOV013')
UNION ALL SELECT pp.id, 'REGION_GWANGJU' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV010'
UNION ALL SELECT pp.id, 'REGION_JEONNAM' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV011'
UNION ALL SELECT pp.id, 'REGION_GYEONGNAM' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('GOV012', 'GOV018')
UNION ALL SELECT pp.id, 'REGION_JEONBUK' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV014'
UNION ALL SELECT pp.id, 'REGION_DAEGU' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV015'
UNION ALL SELECT pp.id, 'REGION_SEJONG' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV016'
UNION ALL SELECT pp.id, 'REGION_GYEONGBUK' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV017'
UNION ALL SELECT pp.id, 'STATUS_SME_WORKER' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code IN ('GOV013', 'GOV017', 'GOV018')
UNION ALL SELECT pp.id, 'STATUS_SME_WORKER' FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'GOV004' AND pp.gov_matching_ratio = 0.1200;
