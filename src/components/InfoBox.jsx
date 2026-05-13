export default function InfoBox({ children }) {
  return (
    <div className="flex items-center gap-2 bg-[#F6FCFA] border border-[#03BFA5] flex-2 rounded-full px-4 py-2 text-xs text-[#03BFA5] w-fit">
      <div className="w-4 h-4 rounded-full bg-[#03BFA5] text-[#FFFFFF] flex items-center justify-center text-[10px] font-bold shrink-0">
        i
      </div>
      <span className="text-[#000000] text-xs bg-[#EFFFFD] font-regular">{children}</span>
    </div>
  );
}