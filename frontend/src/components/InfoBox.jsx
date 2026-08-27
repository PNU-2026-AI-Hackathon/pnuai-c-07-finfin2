const variants = {
  "mint-rect": {
    wrapper: "h-[46px] rounded-[5px] border-[#03BFA5] bg-[#EFFFFD] text-[18px]",
    icon: "size-[19px] bg-[#03BFA5] text-[#FFFFFF] text-[11px]",
    text: "text-[#454545]",
  },
  "mint-region": {
    wrapper: "h-[46px] rounded-[5px] border-[1.367px] border-[#03BFA5] bg-[#EFFFFD] text-[18px]",
    icon: "size-[21.878px] bg-[#03BFA5] text-[#FFFFFF] text-[12px]",
    text: "text-[#454545]",
  },
  "mint-pill": {
    wrapper: "h-[46px] rounded-full border-[#03BFA5] bg-[#EFFFFD] text-[18px]",
    icon: "size-4 bg-[#03BFA5] text-[#FFFFFF] text-[10px]",
    text: "text-[#454545]",
  },
  "mint-outline": {
    wrapper: "h-[50px] rounded-[10px] border-[#03BFA5] bg-transparent text-[18px]",
    icon: "size-4 bg-[#03BFA5] text-[#FFFFFF] text-[10px]",
    text: "text-[#03BFA5]",
  },
  "blue-outline": {
    wrapper: "h-[50px] rounded-[10px] border-[#2C88D9] bg-transparent text-[18px]",
    icon: "size-4 bg-[#2C88D9] text-[#FFFFFF] text-[10px]",
    text: "text-[#2C88D9]",
  },
};

export default function InfoBox({ children, type = "mint-rect", className = "" }) {
  const variant = variants[type] || variants["mint-rect"];

  return (
    <div className={`inline-flex items-center justify-center gap-[7px] border font-pretendard ${variant.wrapper} ${className}`}>
      <div className={`flex shrink-0 items-center justify-center rounded-full font-bold ${variant.icon}`}>
        i
      </div>
      <span className={`whitespace-nowrap bg-transparent font-normal leading-[1.22] ${variant.text}`}>
        {children}
      </span>
    </div>
  );
}
