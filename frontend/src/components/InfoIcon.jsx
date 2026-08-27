export default function InfoIcon({ text }) {
  if (!text) {
    return (
      <div className="flex size-5 shrink-0 items-center justify-center rounded-[10px] border-2 border-[#03BFA5]">
        <span className="text-[12px] font-bold text-[#03BFA5]">i</span>
      </div>
    );
  }

  return (
    <div className="relative inline-flex items-center group cursor-pointer ml-1">
      
      {/* 기본 느낌표/인포 아이콘 */}
      <div className="flex size-5 shrink-0 items-center justify-center rounded-[10px] border-2 border-[#03BFA5] bg-white transition-colors group-hover:bg-[#EFFFFD]">
        <span className="text-[14px] font-bold text-[#03BFA5]">!</span>
      </div>

        <div className="absolute left-6 top-1/2 -translate-y-1/2 hidden group-hover:flex items-center 
        bg-[#F0FFFE] border border-[#03BFA5] rounded-[4px] px-3 py-1 z-50 shadow-sm transition-all
        whitespace-pre">

        <div className="absolute -left-[5px] top-1/2 -translate-y-1/2 w-2 h-2 bg-[#F0FFFE] border-l border-b border-[#03BFA5] rotate-45"></div>
        
        {/* 설명 텍스트 */}
        <span className="text-[#454545] text-[13px] font-pretendard relative z-10 tracking-tight leading-relaxed">
          {text}
        </span>
      </div>
    </div>
  );
}
