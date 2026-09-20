const KEYWORD_LABELS = {
  REGION_SEOUL: "서울", REGION_BUSAN: "부산", REGION_DAEGU: "대구", REGION_INCHEON: "인천",
  REGION_GWANGJU: "광주", REGION_DAEJEON: "대전", REGION_ULSAN: "울산", REGION_SEJONG: "세종",
  REGION_GYEONGGI: "경기", REGION_GANGWON: "강원", REGION_CHUNGBUK: "충북", REGION_CHUNGNAM: "충남",
  REGION_JEONBUK: "전북", REGION_JEONNAM: "전남", REGION_GYEONGBUK: "경북", REGION_GYEONGNAM: "경남",
  REGION_JEJU: "제주",
  STATUS_UNEMPLOYED: "미취업", STATUS_PART_TIME: "단시간근로", STATUS_SME_WORKER: "중소기업 재직",
  STATUS_MILITARY: "군 복무",
  TERM_OVER_3_YEARS: "3년 초과", TERM_2_TO_3_YEARS: "2~3년", TERM_AROUND_1_YEAR: "1년 내외",
  BENEFIT_MAX_INTEREST: "최고금리 우대", BENEFIT_TAX_FREE: "비과세", BENEFIT_EASY_CONDITION: "간편 가입",
  BENEFIT_GOV_SUBSIDY: "정부 지원금",
  INTEREST_SAVINGS: "예적금", INTEREST_LOAN: "대출",
  BANK_FIRST_TRANSACTION: "첫 거래", BANK_SALARY_TRANSFER: "급여 이체", BANK_CARD_USAGE: "카드 실적",
  BANK_AUTO_TRANSFER: "자동이체", BANK_MARKETING: "마케팅 동의", BANK_REDEPOSIT: "재예치",
  BANK_ONLINE_JOIN: "온라인 가입", BANK_AGE: "연령 우대", BANK_ETC: "기타 우대",
};

const PRODUCT_TYPE_LABELS = {
  DEPOSIT: "예금", SAVING: "적금", POLICY: "정책 상품", SUBSCRIPTION: "청약저축",
};

const RESERVE_TYPE_LABELS = {
  FIXED: "정액적립식", FREE: "자유적립식",
};

const RESERVE_TYPE_SHORT_LABELS = {
  FIXED: "정액", FREE: "자유",
};

const INTEREST_TYPE_LABELS = {
  SINGLE_INTEREST: "단리", COMPOUND_INTEREST: "복리",
};

export function keywordLabel(code) {
  return KEYWORD_LABELS[code] || code;
}

export function productTypeLabel(code) {
  return PRODUCT_TYPE_LABELS[code] || code;
}

export function reserveTypeLabel(code) {
  return RESERVE_TYPE_LABELS[code] || code;
}

export function reserveTypeShortLabel(code) {
  return RESERVE_TYPE_SHORT_LABELS[code] || "정액";
}

export function interestTypeLabel(code) {
  return INTEREST_TYPE_LABELS[code] || code;
}
