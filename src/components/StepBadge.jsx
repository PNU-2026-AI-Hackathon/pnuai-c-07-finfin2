export default function StepBadge({ step }) {
  return (
    <span className="inline-block px-2.5 py-0.5 rounded-full border bg-[#F0FFFE] border-[#03BFA5] text-[#03BFA5] text-xs font-inter mb-3">
      STEP{step}
    </span>
  );
}