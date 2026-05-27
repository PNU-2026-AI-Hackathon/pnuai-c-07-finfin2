import StepBadge from "./StepBadge";

export default function StepLayout({ step, title, sub, children }) {
  return (
    <div className="w-full">
      <StepBadge step={step} />
      <h2 className="text-[28px] font-bold text-[#454545] tracking-tight mb-1">{title}</h2>
      <p className="text-[15px] font-regular text-[#7A7A7A] mb-8 leading-relaxed tracking-tight whitespace-pre-line">{sub}</p>
      <div className="w-full">{children}</div>
    </div>
  );
}