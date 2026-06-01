import React from "react";

// 상단 TOP 3 카드
export function TopCard({ rank, title, subtitle, baseRate, maxRate, myRate, tags, isBest, isLoggedIn }) {
  return (
    <div className="px-9 py-7 bg-white rounded-xl border-[2px] border-[#E0DFDF] hover:border-[#03BFA5] hover:shadow-md cursor-pointer shadow-sm flex flex-col justify-between min-h-[300px] w-full transition-all">
      <div>
        <h3 className="text-[22px] font-bold text-[#03BFA5] mb-2.5">TOP {rank}</h3>
        <div className="flex gap-1.5 mb-3 flex-wrap">
          {tags.map((tag, i) => {
            let tagStyle = "bg-[#F5F5F5] text-[#7A7A7A] text-[13px]";
            if (tag.includes('적합도')) tagStyle = "bg-[#FFF4E6] text-[#FF8A00] text-[13px]";
            if (tag.includes('정부기여금') || tag.includes('비과세') || tag.includes('우대')) tagStyle = "bg-[#F2FBF9] text-[#03BFA5] text-[13px]";
            return (
              <span key={i} className={`px-2 py-0.5 rounded-sm text-[11px] font-bold shrink-0 ${tagStyle}`}>{tag}</span>
            );
          })}
        </div>
        <h4 className="text-[25px] font-bold text-[#333333] mb-1.5 break-keep">{title}</h4>
        <p className="text-[12px] text-[#7A7A7A] leading-normal">{subtitle}</p>
      </div>
      <div className="mt-4">
        <div className="flex justify-between items-end mb-3">
          <div>
            <p className="text-[11px] text-[#7A7A7A] mb-0.5">기본 금리</p>
            <p className="text-[20px] font-bold text-[#454545]">연 {baseRate}%</p>
          </div>
          <div className="text-right">
            <p className="text-[11px] text-[#7A7A7A] mb-0.5">최대 수익 효과</p>
            <p className="text-[22px] font-bold text-[#03BFA5]">연 {maxRate}%</p>
          </div>
        </div>
        {/* 💡 비로그인 시 회색 처리 오류 수정 반영 */}
        <div className={`w-full py-2 rounded-full border text-center text-[12px] font-bold ${
          isLoggedIn ? 'border-[#03BFA5] text-[#03BFA5] bg-[#EFFFFD]' : 'border-gray-300 text-gray-400 bg-white'
        }`}>
          내가 달성 가능한 금리 <span className="ml-1 whitespace-nowrap">{isLoggedIn ? `연 ${myRate}%` : '연 ??? %'}</span>
        </div>
      </div>
    </div>
  );
}

// 하단 세로형 리스트 아이템
export function ListItem({ title, subtitle, baseRate, maxRate, myRate, tags, isLoggedIn }) {
  return (
    <div className="p-5 bg-white rounded-xl border border-gray-200 mb-3 flex flex-col lg:flex-row lg:items-center justify-between hover:border-[#03BFA5] hover:shadow-md transition-all cursor-pointer gap-4">
      <div>
        <div className="flex gap-2 mb-2 flex-wrap">
          {tags.map((tag, i) => {
            let tagStyle = "bg-[#F5F5F5] text-[#7A7A7A]";
            if (tag.includes('적합도')) tagStyle = "bg-[#FFF4E6] text-[#FF8A00]";
            if (tag.includes('우대') || tag.includes('비과세')) tagStyle = "bg-[#F2FBF9] text-[#03BFA5]";
            if (tag.includes('내집마련')) tagStyle = "bg-[#F0F2FF] text-[#6B4EFF]";
            return (
              <span key={i} className={`px-2 py-0.5 rounded-[4px] text-[11px] font-bold ${tagStyle}`}>{tag}</span>
            );
          })}
        </div>
        <h4 className="text-[18px] font-bold text-[#333333] mb-1">{title}</h4>
        <p className="text-[13px] text-[#7A7A7A]">{subtitle}</p>
      </div>
      <div className="flex items-center justify-between lg:justify-end gap-6 lg:gap-8 shrink-0 border-t lg:border-none pt-3 lg:pt-0">
        <div className="text-right">
          <p className="text-[11px] text-[#7A7A7A] mb-0.5">기본 금리</p>
          <p className="text-[18px] font-bold text-[#454545] whitespace-nowrap">연 {baseRate}%</p>
        </div>
        <div className="text-right">
          <p className="text-[11px] text-[#7A7A7A] mb-0.5">최대 수익 효과</p>
          <p className="text-[18px] font-bold text-[#03BFA5] whitespace-nowrap">연 {maxRate}%</p>
        </div>
        <div className={`w-[160px] sm:w-[190px] py-2 rounded-full border text-center text-[13px] font-bold shrink-0 ${
          isLoggedIn ? 'border-[#03BFA5] text-[#03BFA5] bg-[#F2FBF9]' : 'border-gray-300 text-gray-400 bg-white'
        }`}>
          내가 달성 가능한 금리 <span className="ml-1 whitespace-nowrap">{isLoggedIn ? `연 ${myRate}%` : '연 ??? %'}</span>
        </div>
      </div>
    </div>
  );
}