export default function StepBadge({ step }) {
  return (
    <span className="inline-flex w-fit items-center justify-center rounded-full border-2 border-[#03BFA5] bg-[#F0FFFE] px-4 py-1 text-[18px] font-semibold text-[#03BFA5] font-pretendard mb-5 shadow-sm">
      STEP{step}
    </span>
  );
}
