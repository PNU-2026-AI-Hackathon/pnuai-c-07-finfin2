export function getProductApplyUrl(product) {
  const directUrl = product?.applyUrl || product?.applicationUrl;

  if (directUrl) {
    return {
      url: directUrl,
      isFallback: false,
    };
  }

  return {
    url: product?.officialChannelUrl || null,
    isFallback: true,
  };
}

export function getProductOfficialChannel(product) {
  return {
    name: product?.providerName || product?.officialChannelName || product?.institution || "기관 공식 채널",
    url: product?.officialChannelUrl || null,
  };
}

export function getProductApplicationBadge(product) {
  if (product?.applicationBadge) return product.applicationBadge;
  if (product?.category?.includes("제 1금융권")) {
    return `은행 공식 홈페이지에서 신청 ∙ ${product.institution}`;
  }
  if (product?.category?.includes("정부") || product?.institution?.includes("정부")) return "사업 공고에서 신청";
  return "공식 페이지에서 신청";
}

export function getProductApplicationBadgeVariant(product) {
  return product?.category?.includes("제 1금융권") ? "bank" : "government";
}

export function openProductApplication(product) {
  const { url } = getProductApplyUrl(product);
  if (!url) return false;
  window.open(url, "_blank", "noopener,noreferrer");
  return true;
}

export function openOfficialChannel(product) {
  const { url } = getProductOfficialChannel(product);
  if (!url) return false;
  window.open(url, "_blank", "noopener,noreferrer");
  return true;
}
