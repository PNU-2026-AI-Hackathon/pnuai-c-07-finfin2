export default function NavButtons({
  onPrev,
  onNext,
  onSubmit,
  isFirst,
  isLast,
  disabled,
  nextLabel,
  submitLabel,
}) {
  const primaryLabel = isLast ? (submitLabel || "완료") : (nextLabel || "다음 단계");

  return (
    <div className="mt-auto flex justify-end gap-4 pt-6">
      <button
        type="button"
        onClick={onPrev}
        disabled={isFirst}
        className="h-[46px] min-w-[109px] rounded-full border border-[#E0DFDF] px-[26px] font-pretendard text-[22px] font-medium text-[#454545] transition-all hover:bg-gray-50 disabled:opacity-30"
      >
        이전
      </button>
      <button
        type="button"
        onClick={isLast ? (onSubmit || onNext) : onNext}
        disabled={disabled}
        className="h-[46px] min-w-[143px] rounded-full bg-[#03BFA5] px-[26px] font-pretendard text-[22px] font-medium text-[#FFFFFF] transition-all hover:bg-[#02a38c] disabled:bg-[#E0DFDF] disabled:text-white disabled:opacity-100 disabled:hover:bg-[#E0DFDF]"
      >
        {primaryLabel}
      </button>
    </div>
  );
}
