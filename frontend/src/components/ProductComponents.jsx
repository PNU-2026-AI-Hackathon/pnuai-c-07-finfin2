import { contributionDisplayValue } from "../utils/recommendationResult";

function handleCardKeyDown(event, onClick) {
  if (!onClick) return;
  if (event.key === "Enter" || event.key === " ") {
    event.preventDefault();
    onClick();
  }
}

function getTagStyle(tag) {
  let tagStyle = "bg-[#F5F5F5] text-[#7A7A7A]";
  if (tag.includes("적합도")) tagStyle = "bg-[#FFF4E6] text-[#FF8A00]";
  if (tag.includes("정부기여금") || tag.includes("비과세") || tag.includes("우대")) tagStyle = "bg-[#F2FBF9] text-[#03BFA5]";
  if (tag.includes("내집마련")) tagStyle = "bg-[#F0F2FF] text-[#6B4EFF]";
  return tagStyle;
}

// 상단 TOP 3 카드
export function TopCard({
  rank,
  title,
  subtitle,
  baseRate,
  maxRate,
  myRate,
  tags,
  isBest,
  isLoggedIn,
  onClick,
  contributionRate,
  maturityContribution,
  contributionCaption,
  showContribution = false,
}) {
  const hasContribution = showContribution && contributionRate && maturityContribution;
  const isInteractive = Boolean(onClick);
  const displayedContributionRate = contributionDisplayValue(contributionRate, isLoggedIn);
  const displayedMaturityContribution = contributionDisplayValue(
    maturityContribution,
    isLoggedIn,
  );

  return (
    <div
      role={isInteractive ? "button" : undefined}
      tabIndex={isInteractive ? 0 : undefined}
      onClick={onClick}
      onKeyDown={(event) => handleCardKeyDown(event, onClick)}
      className={`px-13 py-8 bg-white rounded-xl border-[2px] shadow-sm flex flex-col justify-between min-h-[300px] w-full transition-all ${
        isInteractive ? "hover:shadow-md cursor-pointer" : ""
      } ${
        isBest ? "border-[#03BFA5]" : "border-[#E0DFDF] hover:border-[#03BFA5]"
      }`}
    >
      <div>
        <h3 className="text-[24px] font-bold text-[#03BFA5] mb-2.5">TOP {rank}</h3>
        <div className="flex gap-1.5 mb-6 flex-wrap">
          {tags.map((tag, i) => (
            <span key={i} className={`px-2 py-0.5 rounded-sm text-[15px] shrink-0 ${getTagStyle(tag)}`}>{tag}</span>
          ))}
        </div>
        <h4 className="text-[28px] font-bold text-[#333333] mb-1 break-keep">{title}</h4>
        <p className="text-[14px] text-[#333333] leading-normal mb-8">{subtitle}</p>
      </div>

      {hasContribution ? (
        <div>
          <div className="flex justify-between items-stretch px-2 mb-3">
            <div>
              <p className="text-[14px] text-[#7A7A7A]">기여금 환산 수익률</p>
              <p className="text-[32px] font-bold text-[#454545]">{displayedContributionRate}</p>
            </div>
            <div className="w-px bg-[#E0E0E0]" />
            <div>
              <p className="text-[14px] text-[#7A7A7A]">예상 만기 기여금 총액</p>
              <p className="text-[32px] font-bold text-[#03BFA5]">{displayedMaturityContribution}</p>
            </div>
          </div>
          <p className="text-[13px] text-[#7A7A7A] text-center mb-1">{contributionCaption}</p>
        </div>
      ) : (
        <div>
          <div className="flex justify-between items-stretch px-2 mb-3">
            <div>
              <p className="text-[14px] text-[#7A7A7A]">기본 금리</p>
              <p className="text-[32px] font-bold text-[#454545]">연 {baseRate}%</p>
            </div>
            <div className="w-px bg-[#E0E0E0]" />
            <div>
              <p className="text-[14px] text-[#7A7A7A]">최고 금리</p>
              <p className="text-[32px] font-bold text-[#03BFA5]">연 {maxRate}%</p>
            </div>
          </div>
          {/* 💡 비로그인 시 회색 처리 오류 수정 반영 */}
          <div className={`w-full py-1 rounded-full border text-center text-[15px] ${
            isLoggedIn ? "border-[#03BFA5] text-[#03BFA5] bg-[#EFFFFD]" : "border-gray-300 text-gray-400 bg-white"
          }`}>
            내가 받을 수 있는 금리 <span className="ml-2 font-bold whitespace-nowrap">{isLoggedIn ? `연 ${myRate}%` : "연 ??? %"}</span>
          </div>
        </div>
      )}
    </div>
  );
}

// 하단 세로형 리스트 아이템
export function ListItem({
  title,
  subtitle,
  baseRate,
  maxRate,
  myRate,
  tags,
  isLoggedIn,
  onClick,
  variant = "rate",
  contributionRate,
  maturityContribution,
  contributionCaption,
}) {
  const isContribution = variant === "contribution";
  const isInteractive = Boolean(onClick);
  const displayedContributionRate = contributionDisplayValue(contributionRate, isLoggedIn);
  const displayedMaturityContribution = contributionDisplayValue(
    maturityContribution,
    isLoggedIn,
  );

  return (
    <div
      role={isInteractive ? "button" : undefined}
      tabIndex={isInteractive ? 0 : undefined}
      onClick={onClick}
      onKeyDown={(event) => handleCardKeyDown(event, onClick)}
      className={`py-5 px-8 bg-white rounded-xl border-[2px] border-[#E0DFDF] mb-3 flex flex-col lg:flex-row lg:items-center justify-between transition-all gap-4 ${
        isInteractive ? "hover:border-[#03BFA5] hover:shadow-md cursor-pointer" : ""
      }`}
    >
      <div>
        <div className="flex gap-2 mb-2 flex-wrap">
          {tags.map((tag, i) => (
            <span key={i} className={`px-2 py-0.5 rounded-[4px] text-[15px] ${getTagStyle(tag)}`}>{tag}</span>
          ))}
        </div>
        <h4 className="text-[28px] font-bold text-[#333333] mb-1">{title}</h4>
        <p className="text-[15px] text-[#7A7A7A]">{subtitle}</p>
      </div>

      {isContribution ? (
        <div className="flex flex-col items-end mb-3">
          <div className="flex flex-row gap-10">
            <div className="text-right">
              <p className="text-[14px] text-[#7A7A7A] mb-0.5">기여금 환산 수익률</p>
              <p className="text-[32px] font-bold text-[#454545] whitespace-nowrap">{displayedContributionRate}</p>
            </div>
            <div className="text-right">
              <p className="text-[14px] text-[#7A7A7A] mb-0.5">예상 만기 기여금 총액</p>
              <p className="text-[32px] font-bold text-[#03BFA5] whitespace-nowrap">{displayedMaturityContribution}</p>
            </div>
          </div>
          <p className="text-[13px] text-[#7A7A7A] whitespace-nowrap">{contributionCaption}</p>
        </div>
      ) : (
        <div className="flex flex-col items-end mb-3">
          <div className="flex flex-row gap-10">
            <div className="text-right">
              <p className="text-[14px] text-[#7A7A7A] mb-0.5">기본 금리</p>
              <p className="text-[32px] font-bold text-[#454545] whitespace-nowrap">연 {baseRate}%</p>
            </div>
            <div className="text-right">
              <p className="text-[14px] text-[#7A7A7A] mb-0.5">최고 금리</p>
              <p className="text-[32px] font-bold text-[#03BFA5] whitespace-nowrap">연 {maxRate}%</p>
            </div>
          </div>
          <div className={`w-full py-1 rounded-full border text-center text-[15px] ${
            isLoggedIn ? "border-[#03BFA5] text-[#03BFA5] bg-[#EFFFFD]" : "border-gray-300 text-gray-400 bg-white"
          }`}>
            내가 받을 수 있는 금리 <span className="ml-1 whitespace-nowrap">{isLoggedIn ? `연 ${myRate}%` : "연 ??? %"}</span>
          </div>
        </div>
      )}
    </div>
  );
}
