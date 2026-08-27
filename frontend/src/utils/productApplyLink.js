export function getProductApplyUrl(product) {
  const directUrl = product?.applyUrl || product?.applicationUrl || product?.officialUrl;

  if (directUrl) {
    return {
      url: directUrl,
      isFallback: false,
    };
  }

  const query = [product?.title, product?.institution, "신청"]
    .filter(Boolean)
    .join(" ");

  return {
    url: `https://www.google.com/search?q=${encodeURIComponent(query || "청년 금융상품 신청")}`,
    isFallback: true,
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
  window.open(url, "_blank", "noopener,noreferrer");
}
