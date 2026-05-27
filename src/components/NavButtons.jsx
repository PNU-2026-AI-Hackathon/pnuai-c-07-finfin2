export default function NavButtons({ onPrev, onNext, onSubmit , isFirst, isLast, disabled }) {
  return (
    <div className="flex justify-end gap-2 mt-6">
      <button
        type="button"
        onClick={onPrev}
        disabled={isFirst}
        className="px-5 py-2 rounded-full border border-gray-300 font-[Inter] text-sm text-[#454545] text-medium hover:bg-gray-50 disabled:opacity-30 transition-all"
      >
        이전
      </button>
      <button
        type="button"
        onClick={isLast ? (onSubmit || onNext) : onNext}
        disabled={disabled}
        className="px-5 py-2 rounded-full bg-[#03BFA5] font-[Inter] text-medium text-[#FFFFFF] text-sm hover:bg-[#02a38c] disabled:opacity-40 transition-all"
      >
        {isLast ? "완료" : "다음 단계"}
      </button>
    </div>
  );
}