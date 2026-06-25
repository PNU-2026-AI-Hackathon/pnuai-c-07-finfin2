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




-- FSS representative products generated from api dev DB on 2026-06-25
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010002:00320342', 'e-그린세이브예금', '인터넷,스마트폰

만기 후 1개월: 약정이율의 50%
만기 후 1개월 초과 1년 이내: 약정이율의 30%
만기 후 1년 초과: 약정이율의 10%

1.SC제일은행 최초 거래 신규고객에 대하여 우대 이율을 제공함 (보너스이율0.2%)                     2.SC제일마이백통장에서 출금하여 이 예금을 신규하는경우에 보너스이율을 제공함
(가입기간:1년제/ 보너스이율:0.1% / 만기해약하는 경우에 한해 보너스이율을 적용함)

개인(개인사업자 포함)

디지털채널 전용상품 (인터넷, 모바일뱅킹)', '인터넷, 모바일뱅킹으로 가입 가능한 디지털 전용 상품입니다. SC제일은행 최초 거래 고객 또는 SC제일마이백통장에서 출금하여 신규 시 우대이율(0.2% 또는 0.1%)을 제공합니다. 만기 후 이율은 약정이율의 10%~50%로 차등 적용됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300027000', '미즈월복리정기예금', '영업점,인터넷,스마트폰,기타

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 최고우대금리 0.2% 
 ① 요구불평잔 : 0.2% -300만원이상 0.1%, 500만원이상 0.2%
 ② 신용(체크)카드결제실적 : 0.1% -전월결제금 300만원이상 0.05%, 500만원이상 0.1%

만18세이상 여성으로 실명의 개인 및 개인사업자

1. 가입기간 : 1년이상 3년제
2. 가입금액 : 5백만원이상 최고 50백만원', '만 18세 이상 여성인 개인 및 개인사업자 대상. 영업점, 인터넷, 스마트폰, 기타 채널로 가입 가능. 최고 우대금리 0.2% 제공 (요구불 예금 잔액, 신용/체크카드 결제 실적에 따라).'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300031000', '스마트모아Dream정기예금', '인터넷,스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 1년미만, 1천만원이상 0.10%p
▶ 1년이상, 1천만원이상 0.20%p

개인 및 개인사업자

1. 가입기간 : 1개월이상 3년제
2. 최소가입금액 : 100만원이상', '인터넷, 스마트폰으로 가입 가능. 개인 및 개인사업자 대상. 1개월 이상 3년 이하의 기간으로 가입 가능하며, 최소 가입금액은 100만원입니다. 1년미만, 1천만원이상 가입 시 0.10%p, 1년이상, 1천만원이상 가입 시 0.20%p의 우대금리가 제공됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300035000', '굿스타트예금', '스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 최고우대금리 0.5% 
 ① 첫예금거래 : 0.4% -최근1년동안 정기예금 계좌 신규 또는 해지이력이 없는경우
 ② 개인(신용)정보 수집이용동의 : 0.1% -만기일전일까지 유지시

개인 및 개인사업자

1. 가입기간 : 1년제
2. 가입금액 : 1백만원이상 최고 1억원(1인1계좌)', '가입방법: 스마트폰
가입대상: 개인 및 개인사업자
가입금액: 1백만원 이상 ~ 1억원 이하 (1인1계좌)
우대조건: 첫 예금거래 시 0.4%, 개인(신용)정보 수집이용동의 시 0.1%'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010019:TD11300035000'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010020:101272000057', 'J정기예금
(만기지급식)', '영업점,인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기예금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기예금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

- 아래의 우대요건 충족시 최고 0.5%p 추가 우대 
①비대면 채널 가입시 0.3%제공(신규시제공)  
(단, 이벤트시 디지털 채널에 고시한 우대금리를 추가 적용할 수 있음)
②신규일로부터 만기달 제외한 계약기간의 1/2이상 매월 Jbank로그인 시 0.2%제공(만기시제공)

실명의 
개인 및 
개인사업자

가입금액 : 30만원 이상', '영업점, 인터넷, 스마트폰으로 가입 가능합니다. 비대면 채널 가입 시 0.3%p, Jbank 로그인 시 0.2%p 우대금리를 제공합니다. 가입금액은 30만원 이상이어야 합니다. 만기 후 이자율은 0.1% ~ 50%입니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000', 'JB 다이렉트예금통장
(만기일시지급식)', '인터넷,스마트폰

만기후 1개월 이하 : 만기일 현재 계약기간별 정기예금 실행이율 1/2
만기후 1개월 초과 : 연 0.01%

우대조건
없음

실명의 개인(임의단체 제외

가입금액 1계좌당 1백만원이상
인터넷/스마트폰뱅킹 가입상품', '인터넷, 스마트폰으로 가입 가능하며, 1계좌당 1백만원 이상 가입 가능합니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000', 'JB 123 정기예금
 (만기일시지급식)', '인터넷,스마트폰

만기후 1개월 이하 : 만기일 현재 계약기간별 정기예금 실행이율 1/2
만기후 1개월 초과 : 연 0.01%

자동재예치 우대이율
1회차 0.1%,
2회차 0.2%,
3회차 0.3%

(이벤트우대이율)
2026.5.28~12.31 까지
1)가입일직전 6개월 동안 당행 원화 정기예금 보유이력이 없는 경우 0.50%
2.개인(신용)정보 수집/이용 동의한 경우(단, 상품서비스 안내수단 전체 동의한 경우에 한함) 0.10%

실명의 개인 또는 개인사업자 (1인 다계좌 가입 가능함)

예금의 신규 : 인터넷뱅킹, 모바일뱅킹, 모바일웹, BDT
예금의 해지 : 인터넷뱅킹, 모바일뱅킹, 영업점
가입금액 최저 1백만원이상', '인터넷, 스마트폰으로 가입 가능하며, 개인사업자도 가입할 수 있습니다. 자동 재예치 시 1~3회차에 따라 우대 이율이 제공되며, 특정 기간 내에 당행 정기예금 보유 이력이 없거나 개인정보 수집/이용 동의 시 추가 우대 이율이 적용됩니다. 최소 가입 금액은 1백만원입니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0014674:01013000110000000001', '코드K 정기예금', '스마트폰

만기 후 
- 1개월 이내 : 만기시점 기본금리 X 50%
- 1개월 초과~6개월 이내 : 만기시점 기본금리 X 30%
- 6개월 초과 : 연 0.20%

우대조건 없음

만 17세 이상 실명의 개인 및 개인사업자

가입금액 : 1백만원 이상
가입기간 : 1개월~36개월', '가입방법: 스마트폰
가입대상: 만 17세 이상 실명의 개인 및 개인사업자
가입금액: 1백만원 이상
유의사항: 만기 후 이자 지급 조건이 있음'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0014807:10120116100011', 'Sh첫만남우대예금', '인터넷,스마트폰

* 만기후 
-1개월 이내: 만기당시 일반정기예금(월이자지급식) 계약기간별 기본금리 1/2
-1개월초과~3개월 이내: 만기당시 일반정기예금(월이자지급식) 기본금리의 1/4
- 3개월 초과: 만기당시 보통예금 기본금리

* 최대우대금리:1.05%
1. 첫거래우대 : 1.0% (신규시) 
  - 최근 1년간 수협은행 예적금 활동계좌 미보유 고객포함
2. 마케팅전체동의 : 0.05%(신규시) 
3. 스마트폰뱅킹의 상품알리기 : 0.80%(만기시)

실명의 개인

-1인 1계좌
-최저 100만원 이상', '인터넷, 스마트폰으로 가입 가능하며, 첫 거래 고객 또는 마케팅 동의 시 우대금리가 적용됩니다. 1인 1계좌 제한이 있으며, 최저 100만원 이상 가입 가능합니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0014807:10120116100011'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002', '카카오뱅크 정기예금', '스마트폰

- 만기 후 1개월 이내 : 가입(또는 자동연장)시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입(또는 자동연장)시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

※복잡한 우대조건 없이 가입가능한 정기예금

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 가입금액 : 100만원 이상(원단위)
3. 가입기간 : 1개월 이상 ~ 36개월 이하(월, 일단위 지정 가능)', '스마트폰으로 가입 가능하며, 100만원 이상 가입할 수 있습니다. 만 14세 이상 실명의 개인이 가입 대상이며, 복잡한 우대조건 없이 가입 가능합니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010017:01020400660001', '부산이라 좋다
Big적금', '스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

*우대이율 6개월 미만 최대2.00%, 6개월 이상 2.20%

만 14세이상 실명의 개인고객(1인 1계좌)

1. 가입한도: 월 1천원 이상 100만원 이하 원단위
2. 자유적립식', '가입 대상: 만 14세 이상 실명의 개인 (1인 1계좌)
가입 방법: 스마트폰
가입 한도: 월 1천원 이상 100만원 이하
우대 조건: 만기 후 1년 이내 시 일반정기적금 기본이율의 50%, 만기 후 1년 초과 시 20% 적용. 우대이율은 6개월 미만 최대 2.00%, 6개월 이상 2.20%'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010017:01020400660001'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010017:01020400700001', 'Only One 주거래 우대적금', '영업점,스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

* 우대이율 최대 2.5%
- 공통 우대이율 최대 2%
- 가입자격별 우대이율 최대 0.5%

만14세 이상 실명의 개인(1인 1계좌)

1.가입금(적립)금액 : 월 1천원 이상 50만원 이하
2. 가입기간 : 12개월', '영업점, 스마트폰에서 가입 가능합니다. 만 14세 이상 실명의 개인이 가입할 수 있으며, 1인 1계좌만 가능합니다. 월 1천원 이상 50만원 이하로 가입 가능합니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010017:01020400700001'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010019:TD11330030000', '여행스케치_남도투어적금', '영업점,스마트폰

만기후 1개월 이내: 만기일 당시 최초 가입 기간별 고시금리의 1/2 
만기후 1개월 초과: 0.1%

▶ 최고우대금리 1.9%p 
①예금가입일~만기일전일까지 당행이선정한 전라남도 관광지 방문 인증시 : 최고 1.5%p
②신용(체크)카드사용실적300만원이상:최고 0.3%p
③개인(신용)정보 동의: 0.1%p

만14세이상 개인 및 개인사업자

1. 가입기간 :12개월제,18개월제
2. 가입금액 : 월 5만원 이상 1백만원 이하 (1인1계좌)
※ 18개월 정액식 기본금리 3.3%, 최고금리 5.2%', '영업점, 스마트폰으로 가입 가능하며, 만 14세 이상 개인 및 개인사업자를 대상으로 합니다. 전라남도 관광지 방문 인증, 신용(체크)카드 300만원 이상 사용, 개인(신용)정보 동의 시 최고 우대금리를 제공합니다. 월 5만원 이상 1백만원 이하로 납입 가능하며, 12개월 또는 18개월 가입 가능합니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010020:220002501', 'MZ 플랜적금', '영업점,인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기적금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기적금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

①매월 1회이상 지속적 납입시:1년제: 0.50%
② 목표 금액 달성:0.50%
③ 신용카드,체크카드 합산 사용액 월10만원 이상 사용: 0.50%
* 청년이니까응원합니다 이벤트 : 0.50%

개인 및 개인사업자

월 납입한도 30만원 이하', '가입 대상: 개인 및 개인사업자. 가입 방법: 영업점, 인터넷, 스마트폰. 우대 조건: 매월 1회 이상 납입, 목표 금액 달성, 신용/체크카드 월 10만원 이상 사용, 청년 이벤트 참여 시 추가 금리 제공. 유의사항: 월 납입한도 30만원 이하.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010020:220002501'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010024:21001199', 'BNK 위더스자유적금', '영업점,인터넷,스마트폰,기타

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①ESG 실천 우대금리 1.00%
②신규고객 우대금리 1.00%
- 당행 1년 이내예적금(청약포함)신규해지 이력미보유
③마케팅동의우대금리 0.50%

실명의 개인 및 개인사업자

1.계약기간은 1개월 이상 36개월 이하 월단위로 한다.
2..1인 1계좌로 가입가능
2.매월 최소 1만원 이상, 최고 월 100만원 이하 (천원 단위)', '가입 방법: 영업점, 인터넷, 스마트폰, 기타
가입 대상: 실명의 개인 및 개인사업자
우대 조건: ESG 실천, 신규고객(당행 1년 이내 예적금 신규해지 이력 미보유), 마케팅 동의 시 우대금리 적용
유의사항: 1인 1계좌, 계약기간 1개월~36개월 월단위, 월 최소 1만원 이상 납입, 월 최고 100만원 이하 납입'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010024:21001199'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010024:21001236', '오면우대! 하면우대!
정기적금', '영업점,인터넷,스마트폰

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

신규고객
①적금가입시3.0%
②상품가입 전 마케팅동의시0.1%
③이 적금 신규월 포함 3개월 동안 10만원 이상 경남은행 카드 대금결제시2.0%
기존고객
①급여 또는 연금입금시1.5%
②공과금 자동이체시 2.0%
③경남은행 카드이용시(8회이상&10만원이상 카드대금 결제보유)1.5%
④상품가입 전 마케팅동의시0.1%

실명의 개인

1.계악기간은 1년제로 한다.
2. 적립금액은 매월 10만원이상, 50만원 이하(1만원 이상)', '가입 방법: 영업점, 인터넷, 스마트폰
우대 조건: 신규고객 최대 5.1%, 기존고객 최대 5.1% (각종 조건 충족 시)
가입 대상: 실명의 개인
유의 사항: 계약 기간 1년, 매월 10만원 이상 50만원 이하 납입'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010024:21001236'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010024:21001259', '오늘도세이브적금', '인터넷,스마트폰,기타

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①마케팅동의 0.15%
②목돈마련 축하이율 0.30~1.00%
③친구 추천번호 0.30~0.60%

실명의 개인 및 개인사업자

1. 계약기간은 1개월 이상 6개월 이내 월단위로 한다.
2. 초입금 일 1천원 이상 10만원 이하 자유롭게 저축
3. 최대 저축횟수는 999회 이내', '가입방법: 인터넷, 스마트폰, 기타. 가입대상: 실명의 개인 및 개인사업자. 우대조건: 마케팅동의 시 0.15%, 목돈마련 축하이율 0.30~1.00%, 친구 추천번호 0.30~0.60% 적용. 유의사항: 만기 후 1개월 이내 원리금 지급 시 일반정기예금의 50% 적용, 1개월 초과 시 20% 적용. 계약기간은 1개월 이상 6개월 이내 월단위. 초입금 1천원 이상 10만원 이하 자유 저축. 최대 저축 횟수 999회.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010024:21001259'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010026:01211210113', 'IBK D-day적금(자유적립식)', '스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 1.5%p
1. 목표달성 축하금리 : 연 1.0%p
  - 당행 입출금식 계좌에서 이 적금으로 자동이체를 통해 3회 이상 납입하고 만기일 전일까지 목표금액(신규 시 직접 설정) 이상 납입하는 경우
2. 최초거래고객 우대금리 : 연 0.5%p

실명의 개인
(개인사업자 제외)

1인당 3계좌 가입 가능하며, 계좌당 20만원 이내 납입 가능', '스마트폰으로 가입 가능하며, 개인사업자를 제외한 실명의 개인이 가입할 수 있습니다. 1인당 3계좌까지, 계좌당 20만원 이내로 납입 가능합니다. 목표 금액 이상 납입 및 자동이체 3회 이상 납입 시 최고 연 1.5%p의 우대금리가 제공됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010026:01211210113'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010026:01211210121', 'IBK탄소제로적금(자유적립식)', '스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 2.00%p
1. 에너지 절감 우대금리 : 최대 연 1.00%p
2. 최초거래고객 우대금리 : 연 0.50%p
3. 지로 또는 공과금 자동이체 우대금리 : 연 0.50%p

실명의 개인
(개인사업자 제외)

1인당 1계좌 가입 가능하며, 계좌당 최소 1만원 이상 1백만원까지 납입 가능', '스마트폰으로 가입 가능하며, 1인 1계좌만 개설할 수 있습니다. 최소 1만원부터 1백만원까지 납입 가능하며, 에너지 절감, 최초 거래, 공과금 자동이체 시 우대금리가 적용됩니다. 만기 후에는 약정금리의 20~50%가 적용됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010026:01211210121'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010026:01211210122', 'IBK중기근로자우대적금
(자유적립식)', '영업점,스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 2.20%p
1. 가입시점 중소기업 근로자로 확인된 경우 : 재직기간에 따라 최고 연 1.2%p
2. 당행 급여이체 실적(월50만원 이상) 6개월 이상
   인 경우 : 연 1.0%p

중소기업에서 근무하는
실명의 개인
(개인사업자 제외)

1인당 1계좌 가입 가능하며, 계좌당 100만원까지 납입 가능', '가입 대상: 중소기업 근로자 (개인사업자 제외)
가입 방법: 영업점, 스마트폰
우대 조건: 중소기업 근로자 확인 시(최고 1.2%p), 급여이체 실적(월 50만원 이상) 6개월 이상(1.0%p)
유의사항: 1인당 1계좌, 계좌당 100만원까지 납입 가능'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010026:01211210122'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0010927:010200100104', 'KB 특★한 적금', '스마트폰

- 1개월 이내 : 기본이율 X 50%
- 1개월 초과  ~ 3개월 이내 : 기본이율 X 30%
- 3개월 초과 : 0.1%

항목별 적용 조건 충족시, 최고 연 4.0%p
① 목표달성 축하 우대이율: 최고 연 1.0%p
    50만원 이하: 연 0.5%p, 50만원 초과: 연 1.0%p 
② 별 모으기 우대이율 : 최고 연 1.0%p
    10개: 연 0.5%p, 20개: 연 1.0%p
③ 함께해요 우대이율: 최고 연 2.0%p

실명의 개인

개인사업자, 임의단체 및
공동명의 가입 불가
(1인 최대 3계좌)', '스마트폰으로 가입 가능. 50만원 이하 가입시 연 0.5%p, 50만원 초과 가입시 연 1.0%p 우대이율 제공. 개인사업자, 임의단체, 공동명의 가입 불가. 1인 최대 3계좌까지 가입 가능.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0010927:010200100104'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0013175:10-047-1365-0001', 'NH1934월복리적금', '영업점,인터넷,스마트폰

만기후 1년 이내 : 만기시점 계약기간별 기본금리의 1/2
만기후 1년 초과 : 보통예금 금리

1. 급여실적 : 1.0%p (①/② 중복 적용 불가)
 ① 급여이체 실적 : 1.0%p
 ② 개인사업자 계좌 실적 :1.0%p
2. 비대면 채널 이체 실적 : 0.3%p
3. 마케팅 동의 : 0.2%p
4. 농업계고 및 청년농부사관학교 졸업자 : 2.0%p

만19세~만34세 개인 및 개인사업자

초입금 및 매회 1만원 이상, 월 50만원 이내 자유적립

급여이체 실적과 개인사업자 계좌 실적 우대금리는 중복 적용 불가

 * 급여입금실적 인정기준
  - 가입기간 12개월 이하 : 3개월 이상 급여이체
  - 가입기간 24개월 이하 : 12개월 이상 급여이체', '가입대상: 만19세~만34세 개인 및 개인사업자 (농업계고 및 청년농부사관학교 졸업자 우대)
가입방법: 영업점, 인터넷, 스마트폰
우대조건: 급여이체, 비대면 채널 이체, 마케팅 동의 시 금리 우대
유의사항: 초입금 및 매회 1만원 이상, 월 50만원 이내 자유적립. 급여이체 실적과 개인사업자 계좌 실적 우대금리는 중복 적용 불가.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0014674:01012000200000000004', '궁금한 적금', '스마트폰

만기 후 
- 1개월 이내: 만기시점 기본금리 X 50% 
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

입금할 때마다 우대금리를 랜덤으로 제공하며, 입금 시 제공되는 우대금리를 누적으로 합산하여 만기 해지 시 적용 (최고 연 6.0%)

만 17세 이상 실명의 개인 및 개인사업자

가입금액: 0원
가입기간: 31일
(1인 최대 1계좌)
적립방법 : 연결계좌를 통한 직접 입금만 가능하며, 입금은 신규일부터 만기일 전일까지 1일 1회 가능
납입금액 : 1일(1회) 최소 100원, 최대 5만원', '스마트폰으로 가입 가능하며, 만 17세 이상 개인 및 개인사업자 대상입니다. 1일 1회 최대 5만원까지 납입 가능합니다. 만기 후 이자는 경과 기간에 따라 차등 지급되며, 최고 연 6.0%의 우대금리를 받을 수 있습니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0014674:01012000200000000004'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0014674:01012000200000000006', '마이키즈 적금', '스마트폰

만기 후
- 1개월 이내: 만기시점 기본금리 X 50%
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

1. 입금실적에 따라 우대금리 적용
2. 금리쿠폰을 입력시 우대금리 적용

만 17세 미만의 실명의 개인

가입금액: 0원 / 최대 납입금액 : 월 30만원 
가입기간 : 1년,2년,3년,4년,5년
 ㅇ 4년 금리: 최저 연 3.3% ~ 최고 연 8.3%
 ㅇ 5년 금리: 최저 연 3.5% ~ 최고 연 8.5%', '스마트폰으로 가입 가능하며, 만 17세 미만 실명의 개인이 대상입니다. 입금 실적 및 금리 쿠폰 입력 시 우대금리가 적용됩니다. 가입금액은 0원이며, 월 최대 30만원까지 납입 가능합니다. 4년제는 최저 연 3.3%~최고 연 8.3%, 5년제는 최저 연 3.5%~최고 연 8.5% 금리가 적용됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0014807:10141114300011', 'Sh해양플라스틱Zero!적금
(자유적립식)', '영업점,인터넷,스마트폰

* 만기후 1년 이내
 - 만기당시 상호부금 
계약기간별 기본금리 1/2
* 만기후 1년 초과
 - 만기당시 보통예금 기본금리

* 최대우대금리:0.5%
1. 해양플라스틱감축서약 : 0.1% (신규시) 
2. 봉사활동 또는 상품홍보 : 0.2% (만기시) 
3. 입출금통장 최초신규 : 0.2% (만기시)
4. 자동이체 출금실적 : 0.2% (만기시)
 - 수협신용카드 / 당행 펀드 / 수협체크카드

실명의 개인

- 1인 1계좌 
- 월 가입한도 : 20만원', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 해양플라스틱감축서약, 봉사활동 또는 상품홍보, 입출금통장 최초신규, 자동이체 출금실적
가입대상: 실명의 개인
유의사항: 1인 1계좌, 월 가입한도 20만원'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0014807:10141114300011'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0005', '카카오뱅크 26주적금', '스마트폰

- 만기 후 1개월 이내 : 가입시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

자동이체 연속 성공 우대금리 제공 : 최고 연 3.00%p
- 제공조건
① 7주차까지 자동이체 납입을 연속 성공하고 만기해지 하는 경우 연 1.00%p 제공
② 26주차까지 자동이체 납입을 연속 성공하고 만기해지 하는 경우 연 2.00%p 추가 제공
- 유의사항 : 자동이체 실패한 주차를 빈자리채우기 하여도 성공으로 인정되지 않음

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 가입금액 : 1천원, 2천원, 3천원, 5천원, 1만원
3. 가입기간 : 6개월
4. 26주적금서비스(자동이체)를 통해서 납입이 가능하며, 그 외의 입금은 모두 제한됨
(단, 26주적금 서비스를 통한 납입 실패 시 빈자리 채우기로 납입 가능)', '카카오뱅크 26주적금은 만 14세 이상 실명의 개인이 스마트폰으로 가입할 수 있습니다. 자동이체 납입 시 연속 성공 여부에 따라 최대 연 3.00%p의 우대금리를 제공합니다. 자동이체 실패 시 빈자리 채우기 납입이 가능합니다. 유의사항으로 자동이체 실패한 주차는 빈자리 채우기로 성공 인정되지 않습니다. 최초 1천원부터 1만원까지 자유롭게 가입할 수 있습니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0006', '카카오뱅크 한달적금', '스마트폰

- 만기 후 1개월 이내 : 가입시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

매일/보너스 우대금리 제공 : 최고 연 5.50%p
- 제공조건
① 매일 우대금리 : 매 입금 시 마다 연 0.10%p 제공(최대 연 3.10%p)
② 보너스 우대금리 : 누적하여 5/10/15/20/25/31회 입금 시 해당 우대금리 제공(최대 연 2.40%p)
- 유의사항 : 만기 해지하는 경우에만 제공

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 납입금액 : 1회 100원 이상 3만원 이하(원단위)
3. 가입기간 : 31일
4. 직접 납입을 통해서 1일 1회만 입금 가능하며, 그 외의 입금은 모두 제한됨', '스마트폰으로 가입 가능하며, 1회 100원 이상 3만원 이하로 납입할 수 있습니다. 만 14세 이상 실명의 개인이 가입 대상이며, 만기 해지 시 우대금리가 제공됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0006'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0007', '카카오뱅크 우리아이적금', '스마트폰

- 만기 후 1개월 이내 : 가입시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

자동이체시 우대금리 제공 : 연 4.00%p
 - 제공조건 : 전체 계약월수의 1/2이상을 자동이체로 납입하고 만기 해지하는 경우
 - 유의사항 : 만기 자동연장된 원리금은 우대금리를 제공하지 않음

우리아이통장을 보유한 만 0세 이상 만 17세 미만의 실명의 개인

1. 가입방법: 스마트폰 
2. 가입기간 : 12개월 
3. 월 적립한도: 1천원 이상 월 20만원 이하 원단위(단, 자동연장된 원리금은 월 적립한도에 포함되지 않음)
4. 우리아이서비스를 통해 법정대리인으로 확인된 부 또는 모가 본인을 대리하여 가입 가능', '스마트폰으로 가입 가능하며, 만 0세 이상 만 17세 미만 개인을 대상으로 합니다. 월 1천원 이상 20만원 이하로 납입 가능하며, 전체 계약 월수의 1/2 이상을 자동이체 납입하고 만기 해지 시 우대금리를 제공합니다. 만기 자동연장 시 우대금리가 적용되지 않습니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0017801:1001303001003', '토스뱅크 굴비 적금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

· 만기 해지 시 : 연 2.50% 제공

· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인

· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원
· 우대금리는 만기 해지하는 경우에만 제공됨', '스마트폰으로 가입 가능하며, 토스뱅크 통장 또는 서브 통장을 보유한 실명의 개인이 가입할 수 있습니다. 1인 1계좌만 가능하며, 가입금액은 0원입니다. 우대금리는 만기 해지 시에만 제공됩니다.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0017801:1001303001003'
);
INSERT INTO product (source_id, type, product_code, product_name, content, content_summary)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'FSS:SAVING:020000:0017801:1001303001005', '토스뱅크 아이 적금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

· 적금 가입 시 설정되는 월 단위 자동이체를 모두 성공하는 경우 : 연 2.50% 제공

· 토스뱅크 아이 통장을 보유한 15세 이하 실명의 개인

· 1인 1계좌 
· 가입금액 : 0원
· 우대금리는 만기 해지하는 경우에만 제공됨', '가입방법: 스마트폰. 가입대상: 토스뱅크 아이 통장을 보유한 15세 이하 실명의 개인. 우대조건: 월 단위 자동이체를 모두 성공하는 경우 금리 제공. 유의사항: 1인 1계좌, 가입금액 0원, 우대금리는 만기 해지 시 제공.'
WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND product_code = 'FSS:SAVING:020000:0017801:1001303001005'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010002', '한국스탠다드차타드은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010002'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010017', '부산은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010017'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010019', '광주은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010019'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010020', '제주은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010020'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010022', '전북은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010022'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010024', '경남은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010024'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010026', '중소기업은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010026'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0010927', '국민은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0010927'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0013175', '농협은행주식회사'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0013175'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0014674', '주식회사 케이뱅크'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0014674'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0014807', '수협은행'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0014807'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0015130', '주식회사 카카오뱅크'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0015130'
);
INSERT INTO provider (source_id, code, name)
SELECT (SELECT id FROM product_source WHERE code = 'FSS'), '0017801', '토스뱅크 주식회사'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND code = '0017801'
);

INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '0.50', '6.00', NULL, NULL, NULL, NULL, NULL, 'false', '100', '30000', '14', NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0006'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '0.50'
        AND existing.max_rate IS NOT DISTINCT FROM '6.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '0.70', '6.70', NULL, NULL, NULL, NULL, NULL, 'false', '100', '50000', '17', NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000004'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '0.70'
        AND existing.max_rate IS NOT DISTINCT FROM '6.70'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.40', '2.40', NULL, NULL, NULL, NULL, NULL, 'false', '300000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.40'
        AND existing.max_rate IS NOT DISTINCT FROM '2.40'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.45', '3.95', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.45'
        AND existing.max_rate IS NOT DISTINCT FROM '3.95'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.70', '2.65', NULL, NULL, NULL, NULL, NULL, 'false', '300000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.70'
        AND existing.max_rate IS NOT DISTINCT FROM '2.65'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.80', '4.30', NULL, NULL, NULL, NULL, NULL, 'false', '0', '300000', NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001003'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.80'
        AND existing.max_rate IS NOT DISTINCT FROM '4.30'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.80', '4.30', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.80'
        AND existing.max_rate IS NOT DISTINCT FROM '4.30'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.85', '2.80', NULL, NULL, NULL, NULL, NULL, 'false', '300000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.85'
        AND existing.max_rate IS NOT DISTINCT FROM '2.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.85', '2.80', NULL, NULL, NULL, NULL, NULL, 'false', '300000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.85'
        AND existing.max_rate IS NOT DISTINCT FROM '2.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.90', '2.90', NULL, NULL, NULL, NULL, NULL, 'false', '300000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.90'
        AND existing.max_rate IS NOT DISTINCT FROM '2.90'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '1.90', '7.00', NULL, NULL, NULL, NULL, NULL, 'false', '100000', '500000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '1.90'
        AND existing.max_rate IS NOT DISTINCT FROM '7.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '3.70', NULL, NULL, NULL, NULL, NULL, 'false', '300000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '3.70'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '3.90', NULL, NULL, NULL, NULL, NULL, 'false', '50000', '1000000', '14', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '3.90'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '4.50', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '500000', '14', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400700001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '4.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '5.00', NULL, NULL, NULL, NULL, NULL, 'false', '1000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '5.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '6.00', NULL, NULL, NULL, NULL, NULL, 'false', '500000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '6.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '6.00', NULL, NULL, NULL, NULL, NULL, 'false', '500000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '6.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.00', '6.00', NULL, NULL, NULL, NULL, NULL, 'false', '500000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.00'
        AND existing.max_rate IS NOT DISTINCT FROM '6.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.10', '2.30', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '1000000000', NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.10'
        AND existing.max_rate IS NOT DISTINCT FROM '2.30'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.10', '4.60', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.10'
        AND existing.max_rate IS NOT DISTINCT FROM '4.60'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.10', '4.60', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.10'
        AND existing.max_rate IS NOT DISTINCT FROM '4.60'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.10', '4.60', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.10'
        AND existing.max_rate IS NOT DISTINCT FROM '4.60'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.15', '5.65', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '500000', '19', '34', 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'COMPOUND_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.15'
        AND existing.max_rate IS NOT DISTINCT FROM '5.65'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.20', '4.10', NULL, NULL, NULL, NULL, NULL, 'false', '50000', '1000000', '14', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.20'
        AND existing.max_rate IS NOT DISTINCT FROM '4.10'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.20', '4.20', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '1000000', '14', NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.20'
        AND existing.max_rate IS NOT DISTINCT FROM '4.20'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.20', '4.20', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '1000000', '14', NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.20'
        AND existing.max_rate IS NOT DISTINCT FROM '4.20'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.20', '4.40', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '1000000', '14', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.20'
        AND existing.max_rate IS NOT DISTINCT FROM '4.40'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.20', '4.40', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '1000000', '14', NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.20'
        AND existing.max_rate IS NOT DISTINCT FROM '4.40'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.22', '2.32', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.22'
        AND existing.max_rate IS NOT DISTINCT FROM '2.32'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.30', '5.80', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '500000', '19', '34', 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'COMPOUND_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.30'
        AND existing.max_rate IS NOT DISTINCT FROM '5.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.35', '5.85', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '500000', '19', '34', 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'COMPOUND_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.35'
        AND existing.max_rate IS NOT DISTINCT FROM '5.85'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.45', '3.95', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '600000', NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.45'
        AND existing.max_rate IS NOT DISTINCT FROM '3.95'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.45', '3.95', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '600000', NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.45'
        AND existing.max_rate IS NOT DISTINCT FROM '3.95'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.50', '2.50', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.50'
        AND existing.max_rate IS NOT DISTINCT FROM '2.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.50', '3.55', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', '30000000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014807:10120116100011'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.50'
        AND existing.max_rate IS NOT DISTINCT FROM '3.55'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.50', '4.50', NULL, NULL, NULL, NULL, NULL, 'false', '10000', '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.50'
        AND existing.max_rate IS NOT DISTINCT FROM '4.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.50', '4.70', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '1000000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210122'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.50'
        AND existing.max_rate IS NOT DISTINCT FROM '4.70'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.50', '5.00', NULL, NULL, NULL, NULL, NULL, 'false', '0', '200000', '0', '15', 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001005'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.50'
        AND existing.max_rate IS NOT DISTINCT FROM '5.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.59', '2.69', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.59'
        AND existing.max_rate IS NOT DISTINCT FROM '2.69'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.65', '4.15', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '600000', NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.65'
        AND existing.max_rate IS NOT DISTINCT FROM '4.15'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.75', '4.50', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '100000', NULL, NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.75'
        AND existing.max_rate IS NOT DISTINCT FROM '4.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.75', '4.50', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '100000', NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.75'
        AND existing.max_rate IS NOT DISTINCT FROM '4.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.75', '4.50', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '100000', NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.75'
        AND existing.max_rate IS NOT DISTINCT FROM '4.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.78', '2.88', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.78'
        AND existing.max_rate IS NOT DISTINCT FROM '2.88'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.80', '2.80', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.80'
        AND existing.max_rate IS NOT DISTINCT FROM '2.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.80', '2.80', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.80'
        AND existing.max_rate IS NOT DISTINCT FROM '2.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.80', '2.80', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '17', NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.80'
        AND existing.max_rate IS NOT DISTINCT FROM '2.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.80', '2.80', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.80'
        AND existing.max_rate IS NOT DISTINCT FROM '2.80'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.85', '3.05', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '1000000000', NULL, NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.85'
        AND existing.max_rate IS NOT DISTINCT FROM '3.05'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.85', '3.35', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '200000', NULL, NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.85'
        AND existing.max_rate IS NOT DISTINCT FROM '3.35'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.85', '3.35', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '200000', NULL, NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.85'
        AND existing.max_rate IS NOT DISTINCT FROM '3.35'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.85', '4.35', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '600000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.85'
        AND existing.max_rate IS NOT DISTINCT FROM '4.35'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.90', '2.90', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '1', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '1'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '1'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.90'
        AND existing.max_rate IS NOT DISTINCT FROM '2.90'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '2.95', '4.95', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '300000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '2.95'
        AND existing.max_rate IS NOT DISTINCT FROM '4.95'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.00', '7.00', NULL, NULL, NULL, NULL, NULL, 'false', '1000', '200000', '0', '17', 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.00'
        AND existing.max_rate IS NOT DISTINCT FROM '7.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.00', '8.00', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '300000', NULL, '17', 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.00'
        AND existing.max_rate IS NOT DISTINCT FROM '8.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.00', '8.00', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '300000', NULL, '17', 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.00'
        AND existing.max_rate IS NOT DISTINCT FROM '8.00'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.05', '3.25', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '1000000000', NULL, NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.05'
        AND existing.max_rate IS NOT DISTINCT FROM '3.25'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.10', '3.10', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.10'
        AND existing.max_rate IS NOT DISTINCT FROM '3.10'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.10', '3.10', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '17', NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.10'
        AND existing.max_rate IS NOT DISTINCT FROM '3.10'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.10', '3.10', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '17', NULL, 'false', NULL, NULL, NULL, '3', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '3'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '3'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.10'
        AND existing.max_rate IS NOT DISTINCT FROM '3.10'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.10', '3.30', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.10'
        AND existing.max_rate IS NOT DISTINCT FROM '3.30'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.10', '3.70', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.10'
        AND existing.max_rate IS NOT DISTINCT FROM '3.70'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.15', '3.15', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '17', NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.15'
        AND existing.max_rate IS NOT DISTINCT FROM '3.15'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.15', '5.15', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '300000', NULL, NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.15'
        AND existing.max_rate IS NOT DISTINCT FROM '5.15'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.20', '3.20', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.20'
        AND existing.max_rate IS NOT DISTINCT FROM '3.20'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.20', '3.20', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '17', NULL, 'false', NULL, NULL, NULL, '6', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '6'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '6'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.20'
        AND existing.max_rate IS NOT DISTINCT FROM '3.20'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.20', '3.40', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.20'
        AND existing.max_rate IS NOT DISTINCT FROM '3.40'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.20', '3.40', NULL, NULL, NULL, NULL, NULL, 'false', '5000000', '50000000', '18', NULL, 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'COMPOUND_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.20'
        AND existing.max_rate IS NOT DISTINCT FROM '3.40'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.20', '3.70', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', '100000000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300035000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.20'
        AND existing.max_rate IS NOT DISTINCT FROM '3.70'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.30', '3.50', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.30'
        AND existing.max_rate IS NOT DISTINCT FROM '3.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.30', '3.50', NULL, NULL, NULL, NULL, NULL, 'false', '5000000', '50000000', '18', NULL, 'false', NULL, NULL, NULL, '24', 'false', 'false', 'true', NULL, 'COMPOUND_INTEREST', '24'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '24'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.30'
        AND existing.max_rate IS NOT DISTINCT FROM '3.50'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.30', '8.30', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '300000', NULL, '17', 'false', NULL, NULL, NULL, '36', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '36'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '36'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.30'
        AND existing.max_rate IS NOT DISTINCT FROM '8.30'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.40', '3.40', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '14', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.40'
        AND existing.max_rate IS NOT DISTINCT FROM '3.40'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.40', '3.60', NULL, NULL, NULL, NULL, NULL, 'false', '5000000', '50000000', '18', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'COMPOUND_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.40'
        AND existing.max_rate IS NOT DISTINCT FROM '3.60'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.41', '3.41', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, '17', NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.41'
        AND existing.max_rate IS NOT DISTINCT FROM '3.41'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.45', '3.75', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '1000000000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.45'
        AND existing.max_rate IS NOT DISTINCT FROM '3.75'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.65', '4.15', NULL, NULL, NULL, NULL, NULL, 'false', NULL, '200000', NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.65'
        AND existing.max_rate IS NOT DISTINCT FROM '4.15'
  );
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, gov_contribution_rate,
    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
    exclude_from_rate_comparison, min_monthly_limit, max_monthly_limit, min_age, max_age,
    allows_military_age_extension, military_max_age, earn_max_amt, earn_percent, min_tenure_months,
    requires_homeless, requires_householder, is_joinable, apply_url, intr_rate_type, save_trm
)
SELECT p.id, pr.id, '3.66', '3.66', NULL, NULL, NULL, NULL, NULL, 'false', '1000000', NULL, NULL, NULL, 'false', NULL, NULL, NULL, '12', 'false', 'false', 'true', NULL, 'SINGLE_INTEREST', '12'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND NOT EXISTS (
      SELECT 1 FROM product_properties existing
      WHERE existing.product_id = p.id
        AND existing.provider_id = pr.id
        AND existing.save_trm IS NOT DISTINCT FROM '12'
        AND existing.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
        AND existing.base_rate IS NOT DISTINCT FROM '3.66'
        AND existing.max_rate IS NOT DISTINCT FROM '3.66'
  );

INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_CARD_USAGE'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_CARD_USAGE'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.75'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '2.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.05'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.05'
  AND pp.max_rate IS NOT DISTINCT FROM '3.25'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300035000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.40'
  AND pp.max_rate IS NOT DISTINCT FROM '2.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.70'
  AND pp.max_rate IS NOT DISTINCT FROM '2.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.66'
  AND pp.max_rate IS NOT DISTINCT FROM '3.66'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '2.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '4.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014807:10120116100011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '3.55'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_FIRST_TRANSACTION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001003'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_FIRST_TRANSACTION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_SALARY_TRANSFER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_SALARY_TRANSFER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_SALARY_TRANSFER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_SALARY_TRANSFER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BANK_SALARY_TRANSFER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BANK_SALARY_TRANSFER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_EASY_CONDITION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_EASY_CONDITION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_EASY_CONDITION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_EASY_CONDITION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_EASY_CONDITION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_EASY_CONDITION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_EASY_CONDITION'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_EASY_CONDITION'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.95'
  AND pp.max_rate IS NOT DISTINCT FROM '4.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '4.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210122'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_GOV_SUBSIDY'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_GOV_SUBSIDY'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.75'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.05'
  AND pp.max_rate IS NOT DISTINCT FROM '3.25'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400700001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300035000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.95'
  AND pp.max_rate IS NOT DISTINCT FROM '4.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.66'
  AND pp.max_rate IS NOT DISTINCT FROM '3.66'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '4.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210122'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.41'
  AND pp.max_rate IS NOT DISTINCT FROM '3.41'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '3.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000004'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '0.70'
  AND pp.max_rate IS NOT DISTINCT FROM '6.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '8.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '8.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '8.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014807:10120116100011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '3.55'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0006'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '0.50'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001003'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'BENEFIT_MAX_INTEREST'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001005'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'BENEFIT_MAX_INTEREST'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400700001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.22'
  AND pp.max_rate IS NOT DISTINCT FROM '2.32'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.59'
  AND pp.max_rate IS NOT DISTINCT FROM '2.69'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.78'
  AND pp.max_rate IS NOT DISTINCT FROM '2.88'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.40'
  AND pp.max_rate IS NOT DISTINCT FROM '2.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.70'
  AND pp.max_rate IS NOT DISTINCT FROM '2.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.66'
  AND pp.max_rate IS NOT DISTINCT FROM '3.66'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '2.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.41'
  AND pp.max_rate IS NOT DISTINCT FROM '3.41'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '3.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'INTEREST_SAVINGS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'INTEREST_SAVINGS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_BUSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_BUSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_CHUNGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_CHUNGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_CHUNGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_CHUNGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_CHUNGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_CHUNGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_CHUNGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_CHUNGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_CHUNGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_CHUNGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_CHUNGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_CHUNGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_DAEGU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_DAEGU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_DAEGU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_DAEGU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_DAEGU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_DAEGU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_DAEJEON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_DAEJEON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_DAEJEON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_DAEJEON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_DAEJEON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_DAEJEON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GANGWON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GANGWON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GANGWON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GANGWON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GANGWON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GANGWON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GWANGJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300035000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GWANGJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GWANGJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GWANGJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GWANGJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GWANGJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GWANGJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GWANGJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.66'
  AND pp.max_rate IS NOT DISTINCT FROM '3.66'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '2.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGGI'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGGI'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGGI'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGGI'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGGI'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGGI'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_GYEONGNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_GYEONGNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_INCHEON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_INCHEON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_INCHEON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_INCHEON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_INCHEON'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_INCHEON'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.40'
  AND pp.max_rate IS NOT DISTINCT FROM '2.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.70'
  AND pp.max_rate IS NOT DISTINCT FROM '2.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEJU'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEJU'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONBUK'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONBUK'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.22'
  AND pp.max_rate IS NOT DISTINCT FROM '2.32'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.59'
  AND pp.max_rate IS NOT DISTINCT FROM '2.69'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.78'
  AND pp.max_rate IS NOT DISTINCT FROM '2.88'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_JEONNAM'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_JEONNAM'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEJONG'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEJONG'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEJONG'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEJONG'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEJONG'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEJONG'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_SEOUL'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_SEOUL'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_ULSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_ULSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_ULSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_ULSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'REGION_ULSAN'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'REGION_ULSAN'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.40'
  AND pp.max_rate IS NOT DISTINCT FROM '2.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.70'
  AND pp.max_rate IS NOT DISTINCT FROM '2.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.95'
  AND pp.max_rate IS NOT DISTINCT FROM '4.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_PART_TIME'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_PART_TIME'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.95'
  AND pp.max_rate IS NOT DISTINCT FROM '4.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.66'
  AND pp.max_rate IS NOT DISTINCT FROM '3.66'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '2.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '4.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210122'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.41'
  AND pp.max_rate IS NOT DISTINCT FROM '3.41'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '3.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000004'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '0.70'
  AND pp.max_rate IS NOT DISTINCT FROM '6.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_SME_WORKER'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0006'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '0.50'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_SME_WORKER'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_UNEMPLOYED'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '8.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_UNEMPLOYED'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_UNEMPLOYED'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '8.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_UNEMPLOYED'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_UNEMPLOYED'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '8.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_UNEMPLOYED'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_UNEMPLOYED'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_UNEMPLOYED'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'STATUS_UNEMPLOYED'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001005'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'STATUS_UNEMPLOYED'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.85'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.35'
  AND pp.max_rate IS NOT DISTINCT FROM '5.85'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.15'
  AND pp.max_rate IS NOT DISTINCT FROM '3.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '8.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '8.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '24'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_2_TO_3_YEARS'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '36'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_2_TO_3_YEARS'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.75'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '2.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '3.05'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010002'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010002:00320342'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.05'
  AND pp.max_rate IS NOT DISTINCT FROM '3.25'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400660001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010017'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010017:01020400700001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300027000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.30'
  AND pp.max_rate IS NOT DISTINCT FROM '3.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.22'
  AND pp.max_rate IS NOT DISTINCT FROM '2.32'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.59'
  AND pp.max_rate IS NOT DISTINCT FROM '2.69'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300031000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.78'
  AND pp.max_rate IS NOT DISTINCT FROM '2.88'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010019:TD11300035000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010019'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010019:TD11330030000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.20'
  AND pp.max_rate IS NOT DISTINCT FROM '4.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.40'
  AND pp.max_rate IS NOT DISTINCT FROM '2.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.70'
  AND pp.max_rate IS NOT DISTINCT FROM '2.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010020:101272000057'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010020'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010020:220002501'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.95'
  AND pp.max_rate IS NOT DISTINCT FROM '4.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.66'
  AND pp.max_rate IS NOT DISTINCT FROM '3.66'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '2.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010022'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.10'
  AND pp.max_rate IS NOT DISTINCT FROM '4.60'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001199'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001236'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.90'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010024'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010024:21001259'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.75'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.85'
  AND pp.max_rate IS NOT DISTINCT FROM '4.35'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.45'
  AND pp.max_rate IS NOT DISTINCT FROM '3.95'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210113'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210121'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.50'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010026'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010026:01211210122'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '4.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0010927'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0010927:010200100104'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.30'
  AND pp.max_rate IS NOT DISTINCT FROM '5.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0013175'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0013175:10-047-1365-0001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'COMPOUND_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.15'
  AND pp.max_rate IS NOT DISTINCT FROM '5.65'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.41'
  AND pp.max_rate IS NOT DISTINCT FROM '3.41'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.80'
  AND pp.max_rate IS NOT DISTINCT FROM '2.80'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014674:01013000110000000001'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000004'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '0.70'
  AND pp.max_rate IS NOT DISTINCT FROM '6.70'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014674'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014674:01012000200000000006'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '8.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0014807:10120116100011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '3.55'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0014807'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0014807:10141114300011'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.65'
  AND pp.max_rate IS NOT DISTINCT FROM '4.15'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.40'
  AND pp.max_rate IS NOT DISTINCT FROM '3.40'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.90'
  AND pp.max_rate IS NOT DISTINCT FROM '2.90'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '3'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.10'
  AND pp.max_rate IS NOT DISTINCT FROM '3.10'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.20'
  AND pp.max_rate IS NOT DISTINCT FROM '3.20'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0005'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.00'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0006'
  AND pp.save_trm IS NOT DISTINCT FROM '1'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '0.50'
  AND pp.max_rate IS NOT DISTINCT FROM '6.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0015130'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0015130:10-01-30-355-0007'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '3.00'
  AND pp.max_rate IS NOT DISTINCT FROM '7.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001003'
  AND pp.save_trm IS NOT DISTINCT FROM '6'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '1.80'
  AND pp.max_rate IS NOT DISTINCT FROM '4.30'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
INSERT INTO product_property_keyword (product_property_id, keyword_code)
SELECT pp.id, 'TERM_AROUND_1_YEAR'
FROM product p
JOIN provider pr ON pr.source_id = (SELECT id FROM product_source WHERE code = 'FSS') AND pr.code = '0017801'
JOIN product_properties pp ON pp.product_id = p.id AND pp.provider_id = pr.id
WHERE p.source_id = (SELECT id FROM product_source WHERE code = 'FSS')
  AND p.product_code = 'FSS:SAVING:020000:0017801:1001303001005'
  AND pp.save_trm IS NOT DISTINCT FROM '12'
  AND pp.intr_rate_type IS NOT DISTINCT FROM 'SINGLE_INTEREST'
  AND pp.base_rate IS NOT DISTINCT FROM '2.50'
  AND pp.max_rate IS NOT DISTINCT FROM '5.00'
  AND NOT EXISTS (
      SELECT 1 FROM product_property_keyword existing
      WHERE existing.product_property_id = pp.id AND existing.keyword_code = 'TERM_AROUND_1_YEAR'
  );
