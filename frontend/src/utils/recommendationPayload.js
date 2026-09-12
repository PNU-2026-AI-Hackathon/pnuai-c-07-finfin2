export const CATEGORY_ALIASES = {
  regions: ["거주지역"],
  status: ["현재신분"],
  savingPeriod: ["저축기간"],
  benefits: ["혜택선택", "핵심혜택"],
  bankRelation: ["우대거래", "은행거래"],
};

const HIDDEN_BANK_RELATION_OPTIONS = new Set(["첫거래고객", "재예치"]);

const BANK_CODE_BY_LABEL = {
  KB국민: "0010927",
  KB국민은행: "0010927",
  신한: "0011625",
  신한은행: "0011625",
  하나: "0013909",
  하나은행: "0013909",
  우리: "0010001",
  우리은행: "0010001",
  SC제일: "0010002",
  SC제일은행: "0010002",
  iM뱅크: "0010016",
  카카오뱅크: "0015130",
  토스뱅크: "0017801",
  케이뱅크: "0014674",
  NH농협: "0013175",
  NH농협은행: "0013175",
  Sh수협: "0014807",
  Sh수협은행: "0014807",
  IBK기업: "0010026",
  IBK기업은행: "0010026",
  BNK부산: "0010017",
  부산은행: "0010017",
  광주은행: "0010019",
  제주은행: "0010020",
  전북은행: "0010022",
  BNK경남은행: "0010024",
  경남은행: "0010024",
};

export function normalizeCategoryName(value = "") {
  return value.replace(/[\s·_()-]/g, "").toLowerCase();
}

function findCategory(categories, aliases) {
  const normalizedAliases = aliases.map(normalizeCategoryName);
  return categories.find((category) =>
    normalizedAliases.includes(normalizeCategoryName(category?.categoryName)),
  );
}

export function findCategoryByGroup(categories, groupKey) {
  return findCategory(categories || [], CATEGORY_ALIASES[groupKey] || [groupKey]);
}

export function mapRecommendCategories(payload, extras = {}) {
  const categories = Array.isArray(payload)
    ? payload
    : Array.isArray(payload?.data)
      ? payload.data
      : [];

  const matched = Object.fromEntries(
    Object.entries(CATEGORY_ALIASES).map(([key, aliases]) => [
      key,
      findCategory(categories, aliases),
    ]),
  );

  return {
    regions: matched.regions?.options || [],
    status: matched.status?.options || [],
    savingPeriod: matched.savingPeriod?.options || [],
    benefits: matched.benefits?.options || [],
    bankRelation: (matched.bankRelation?.options || []).filter(
      (option) => !HIDDEN_BANK_RELATION_OPTIONS.has(
        normalizeCategoryName(option?.optionValue),
      ),
    ),
    categoryIds: Object.fromEntries(
      Object.entries(matched).map(([key, category]) => [key, category?.categoryId]),
    ),
    ...extras,
  };
}

function toNumber(value) {
  if (value === "" || value === null || value === undefined) return null;
  const number = Number(value);
  return Number.isFinite(number) ? number : null;
}

function toWonFromTenThousand(value) {
  const number = toNumber(value);
  return number === null ? null : Math.round(number * 10_000);
}

function toTenThousandFromWon(value) {
  const number = toNumber(value);
  return number === null ? null : Math.round(number / 10_000);
}

function toBirthdate(data) {
  if (!data.birthYear || !data.birthMonth || !data.birthDay) return null;
  const month = String(data.birthMonth).padStart(2, "0");
  const day = String(data.birthDay).padStart(2, "0");
  return `${data.birthYear}-${month}-${day}`;
}

function toHouseholdIncomePercent(value) {
  const match = String(value || "").match(/(\d+)\s*%/);
  return match ? Number(match[1]) : null;
}

function toBankCodes(values = []) {
  return values.map((value) => BANK_CODE_BY_LABEL[value] || value);
}

const BANK_LABEL_BY_CODE = Object.entries(BANK_CODE_BY_LABEL).reduce((map, [label, code]) => {
  const existing = map[code];
  if (!existing || (existing.endsWith("은행") && !label.endsWith("은행"))) {
    map[code] = label;
  }
  return map;
}, {});

function fromBankCodes(values = []) {
  return values.map((value) => BANK_LABEL_BY_CODE[value] || value);
}

function selectedOptions(data, categoryIds) {
  const fields = [
    ["regions", data.region ? [data.region] : []],
    ["status", data.status || []],
    ["savingPeriod", data.savingPeriod || []],
    ["benefits", data.benefits || []],
    ["bankRelation", data.bankRelation || []],
  ];

  const seen = new Set();
  return fields.flatMap(([field, values]) => {
    const categoryId = toNumber(categoryIds?.[field]);
    if (categoryId === null) return [];

    return values.flatMap((value) => {
      const optionId = toNumber(value);
      const key = `${categoryId}:${optionId}`;
      if (optionId === null || seen.has(key)) return [];
      seen.add(key);
      return [{ categoryId, optionId }];
    });
  });
}

export function buildRecommendationRequest(data, categories) {
  const employmentMonths = toNumber(data.employmentMonths);

  return {
    options: selectedOptions(data, categories?.categoryIds),
    detailedOptions: {
      birthdate: toBirthdate(data),
      annualIncome: toWonFromTenThousand(data.income),
      householdSize: toNumber(data.householdCount) ?? 1,
      householdIncomePercent: toHouseholdIncomePercent(data.incomeLevel),
      tenureMonths: employmentMonths !== null && employmentMonths >= 0 ? employmentMonths : null,
      isFirstJob: data.isFirstJob ? true : null,
      isHomeless:
        data.housingStatus === "무주택"
          ? true
          : data.housingStatus === "유주택"
            ? false
            : null,
      isHouseholder: data.isTenant ? true : null,
      monthlySavingsGoal: toWonFromTenThousand(data.monthlyAmount),
      neverUsedBanks: toBankCodes(data.firstBanks || []),
      maturedSavingBanks: toBankCodes(data.maturedBanks || []),
      selectedInterestRateOptions: [],
    },
  };
}

export function buildRecommendationRequestFromProfile(profile, categories) {
  const categoryIdByOptionId = new Map();
  (categories || []).forEach((category) => {
    (category.options || []).forEach((option) => {
      categoryIdByOptionId.set(option.optionId, category.categoryId);
    });
  });

  const options = (profile?.selectedOptionIds || []).flatMap((optionId) => {
    const categoryId = categoryIdByOptionId.get(optionId);
    return categoryId === undefined ? [] : [{ categoryId, optionId }];
  });

  return {
    options,
    detailedOptions: {
      birthdate: profile?.birthdate ?? null,
      annualIncome: toWonFromTenThousand(profile?.annualIncome),
      householdSize: profile?.householdSize ?? null,
      householdIncomePercent: profile?.householdIncomePercent ?? null,
      tenureMonths: profile?.tenureMonths ?? null,
      isFirstJob: profile?.isFirstJob ?? null,
      isHomeless: profile?.isHomeless ?? null,
      isHouseholder: profile?.isHouseholder ?? null,
      monthlySavingsGoal: toWonFromTenThousand(profile?.monthlySavingsGoal),
      neverUsedBanks: toBankCodes(profile?.neverUsedBanks ?? []),
      maturedSavingBanks: toBankCodes(profile?.maturedSavingBanks ?? []),
      selectedInterestRateOptions: [],
    },
  };
}

export function buildProfileUpdateFromRequest(request) {
  const options = Array.isArray(request?.options) ? request.options : [];
  const detailed = request?.detailedOptions || {};

  return {
    birthdate: detailed.birthdate ?? null,
    annualIncome: toTenThousandFromWon(detailed.annualIncome),
    householdSize: detailed.householdSize ?? null,
    householdIncomePercent: detailed.householdIncomePercent ?? null,
    tenureMonths: detailed.tenureMonths ?? null,
    isFirstJob: detailed.isFirstJob ?? null,
    isHomeless: detailed.isHomeless ?? null,
    isHouseholder: detailed.isHouseholder ?? null,
    monthlySavingsGoal: toTenThousandFromWon(detailed.monthlySavingsGoal),
    mainBanks: detailed.mainBanks ?? [],
    neverUsedBanks: fromBankCodes(detailed.neverUsedBanks ?? []),
    maturedSavingBanks: fromBankCodes(detailed.maturedSavingBanks ?? []),
    selectedOptionIds: options.map((option) => option.optionId),
  };
}
