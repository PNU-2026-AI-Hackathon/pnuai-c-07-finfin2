export default function StepBadge({ step }) {
  return (
    <span className="inline-block px-3 py-1.5 rounded-full border-2 bg-[#F0FFFE] border-[#03BFA5] text-[#03BFA5] text-[18px] font-inter tracking-wider mb-4 shadow-sm">
      STEP{step}
    </span>
  );
}