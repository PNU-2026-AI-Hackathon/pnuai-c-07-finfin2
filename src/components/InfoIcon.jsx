export default function InfoIcon({ text }) {
  if (!text) {
    return (
      <div className="w-[15px] h-[15px] rounded-full border border-[#03BFA5] flex items-center justify-center shrink-0">
        <span className="text-[#03BFA5] text-[10px] font-bold">i</span>
      </div>
    );
  }

  return (
    <div className="relative inline-flex items-center group cursor-pointer ml-1">
      
      {/* 기본 느낌표/인포 아이콘 */}
      <div className="w-[15px] h-[15px] rounded-full border border-[#03BFA5] flex items-center justify-center bg-white shrink-0 transition-colors group-hover:bg-[#EFFFFD]">
        <span className="text-[#03BFA5] text-[10px] font-bold">i</span>
      </div>

        <div className="absolute left-6 top-1/2 -translate-y-1/2 hidden group-hover:flex items-center 
        bg-[#F0FFFE] border border-[#03BFA5] rounded-[4px] px-3 py-1 z-50 shadow-sm transition-all
        whitespace-pre">

        <div className="absolute -left-[5px] top-1/2 -translate-y-1/2 w-2 h-2 bg-[#F0FFFE] border-l border-b border-[#03BFA5] rotate-45"></div>
        
        {/* 설명 텍스트 */}
        <span className="text-[#454545] text-[11px] font-normal relative z-10 tracking-tight leading-relaxed">
          {text}
        </span>
      </div>
    </div>
  );
}