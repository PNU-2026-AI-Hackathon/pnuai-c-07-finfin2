export default function NavButtons({ onPrev, onNext, isFirst, isLast, disabled }) {
  return (
    <div className="flex justify-end gap-2 mt-6">
      <button
        type="button"
        onClick={onPrev}
        disabled={isFirst}
        className="px-5 py-2 rounded-full border border-gray-300 text-sm text-[#454545] text-medium hover:bg-gray-50 disabled:opacity-30 transition-all"
      >
        이전
      </button>
      <button
        type="button"
        onClick={onNext}
        disabled={disabled}
        className="px-5 py-2 rounded-full bg-[#03BFA5] text-medium text-[#FFFFFF] text-sm hover:bg-[#03BFA5] disabled:opacity-40 transition-all"
      >
        {isLast ? "완료" : "다음 단계"}
      </button>
    </div>
  );
}