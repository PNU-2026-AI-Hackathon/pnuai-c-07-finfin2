export default function Tag({ label, selected, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-pressed={selected}
      className={`h-[46px] rounded-full border-2 px-[18px] text-[20px] font-medium transition-all cursor-pointer ${
        selected
          ? "bg-[#03BFA5] text-white border-[#03BFA5]"
          : "bg-white text-[#454545] border-[#E0DFDF] hover:border-[#03BFA5]"
      }`}
    >
      #{label}
    </button>
  );
}
