import { useEffect, useMemo, useState } from "react";
import { useAuth } from "../context/AuthContext";
import client, { withAuth } from "../api/client";
import { MOCK_PROFILE, MOCK_OPTION_CATEGORIES, MOCK_FAVORITES } from "../data/mypage";
import { buildRecommendationRequestFromProfile, findCategoryByGroup, normalizeCategoryName } from "../utils/recommendationPayload";

const TAG_GROUP_KEYS = ["savingPeriod", "status", "benefits", "bankRelation"];

export function splitTrailingParen(value) {
  if (!value) return { main: "", caption: "" };
  const match = value.match(/^(.*?)(\([^)]*\))\s*$/);
  if (!match) return { main: value, caption: "" };
  return { main: match[1].trim(), caption: match[2] };
}

function isMockMode() {
  return import.meta.env.DEV && new URLSearchParams(window.location.search).get("mock") === "true";
}

function buildOptionTagsByCategory(profile, categories) {
  const groups = { savingPeriod: [], status: [], benefits: [], bankRelation: [] };
  if (!profile || !categories) return groups;

  const groupKeyByNormalizedCategoryName = {};
  TAG_GROUP_KEYS.forEach((key) => {
    const category = findCategoryByGroup(categories, key);
    if (category) groupKeyByNormalizedCategoryName[normalizeCategoryName(category.categoryName)] = key;
  });

  const optionMap = {};
  categories.forEach((category) => {
    (category.options || []).forEach((option) => {
      optionMap[option.optionId] = { categoryName: category.categoryName, optionValue: option.optionValue };
    });
  });

  (profile.selectedOptionIds || []).forEach((id) => {
    const option = optionMap[id];
    if (!option) return;
    const groupKey = groupKeyByNormalizedCategoryName[normalizeCategoryName(option.categoryName)];
    if (groupKey) groups[groupKey].push(option.optionValue);
  });

  return groups;
}

export function findCategoryOptions(categories, groupKey) {
  return findCategoryByGroup(categories, groupKey)?.options || [];
}

export function findSelectedOptionIds(categories, groupKey, selectedOptionIds) {
  const ids = new Set(findCategoryOptions(categories, groupKey).map((option) => option.optionId));
  return (selectedOptionIds || []).filter((id) => ids.has(id));
}

export function replaceCategorySelection(selectedOptionIds, categories, groupKey, newIds) {
  const ids = new Set(findCategoryOptions(categories, groupKey).map((option) => option.optionId));
  const kept = (selectedOptionIds || []).filter((id) => !ids.has(id));
  return [...kept, ...newIds];
}

function buildProfileRequestBody(profile, patch) {
  return {
    birthdate: profile.birthdate,
    annualIncome: profile.annualIncome,
    householdSize: profile.householdSize,
    householdIncomePercent: profile.householdIncomePercent,
    tenureMonths: profile.tenureMonths,
    isFirstJob: profile.isFirstJob,
    isHomeless: profile.isHomeless,
    isHouseholder: profile.isHouseholder,
    monthlySavingsGoal: profile.monthlySavingsGoal,
    mainBanks: profile.mainBanks,
    neverUsedBanks: profile.neverUsedBanks,
    maturedSavingBanks: profile.maturedSavingBanks,
    selectedOptionIds: profile.selectedOptionIds,
    ...patch,
  };
}

function calculateAge(birthdate) {
  const birth = new Date(birthdate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const hadBirthdayThisYear =
    today.getMonth() > birth.getMonth() ||
    (today.getMonth() === birth.getMonth() && today.getDate() >= birth.getDate());
  if (!hadBirthdayThisYear) age -= 1;
  return age;
}

// mock 모드는 실제 백엔드가 없어 display를 재계산할 서버가 없으므로, 미리보기용으로 최소한만 다시 계산한다.
function applyMockPatch(prev, patch, categories) {
  const merged = { ...prev, ...patch };
  const display = { ...prev.display };

  if (patch.birthdate) {
    display.age = calculateAge(patch.birthdate);
  }

  if (patch.selectedOptionIds) {
    const regionOption = findCategoryOptions(categories, "거주지역").find((option) =>
      patch.selectedOptionIds.includes(option.optionId),
    );
    if (regionOption) display.region = regionOption.optionValue;
  }

  if (patch.neverUsedBanks || patch.maturedSavingBanks) {
    display.transactionHistory = {
      firstTransactionBanks: patch.neverUsedBanks ?? prev.neverUsedBanks ?? [],
      redepositBanks: patch.maturedSavingBanks ?? prev.maturedSavingBanks ?? [],
    };
  }

  return { ...merged, display };
}

export default function useMyPage() {
  const { accessToken } = useAuth();
  const mockMode = isMockMode();

  const [profile, setProfile] = useState(mockMode ? MOCK_PROFILE : null);
  const [categories, setCategories] = useState(mockMode ? MOCK_OPTION_CATEGORIES : null);
  const [favorites, setFavorites] = useState(mockMode ? MOCK_FAVORITES.items : []);
  const [showComparisonNotice, setShowComparisonNotice] = useState(
    mockMode ? MOCK_FAVORITES.showComparisonNotice : false,
  );
  const [loading, setLoading] = useState(!mockMode);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (mockMode || !accessToken) return;
    let cancelled = false;

    (async () => {
      setLoading(true);
      setError(null);
      try {
        const [profileRes, categoriesRes] = await Promise.all([
          client.get("/user/me/profile", withAuth(accessToken)),
          client.get("/api/categories", withAuth(accessToken)),
        ]);
        if (cancelled) return;
        setProfile(profileRes.data);
        setCategories(categoriesRes.data || []);

        // /favorites는 프로필 미반영 원본 기록만 내려주므로, 사용자 프로필을 실어
        // /favorites/list(getFavoritesWithProfile)를 호출해 개인화된 fitScore/금리를 받아온다.
        const favoritesRequest = buildRecommendationRequestFromProfile(profileRes.data, categoriesRes.data || []);
        const favoritesRes = await client.post("/favorites/list", favoritesRequest, withAuth(accessToken));
        if (cancelled) return;
        setFavorites(favoritesRes.data?.items || []);
        setShowComparisonNotice(Boolean(favoritesRes.data?.showComparisonNotice));
      } catch (e) {
        if (!cancelled) setError(e);
        console.error("마이페이지 정보를 불러오지 못했습니다:", e);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [accessToken, mockMode]);

  const optionTagsByCategory = useMemo(
    () => buildOptionTagsByCategory(profile, categories),
    [profile, categories],
  );

  const removeFavorite = async (productPropertyId) => {
    const prevFavorites = favorites;
    setFavorites((prev) => prev.filter((item) => item.productPropertyId !== productPropertyId));

    if (mockMode) return;
    try {
      await client.delete(`/favorites/${productPropertyId}`, withAuth(accessToken));
    } catch (e) {
      console.error("찜 삭제에 실패했습니다:", e);
      setFavorites(prevFavorites);
    }
  };

  const updateProfile = async (patch) => {
    if (mockMode) {
      setProfile((prev) => applyMockPatch(prev, patch, categories));
      return { ok: true };
    }

    const body = buildProfileRequestBody(profile, patch);
    try {
      await client.put("/user/me/profile", body, withAuth(accessToken));
      const res = await client.get("/user/me/profile", withAuth(accessToken));
      setProfile(res.data);
      return { ok: true };
    } catch (e) {
      console.error("개인정보 수정에 실패했습니다:", e);
      return { ok: false };
    }
  };

  return {
    profile,
    categories,
    optionTagsByCategory,
    favorites,
    showComparisonNotice,
    loading,
    error,
    removeFavorite,
    updateProfile,
  };
}
