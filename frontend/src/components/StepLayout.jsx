import { useLayoutEffect, useRef, useState } from "react";
import StepBadge from "./StepBadge";

export default function StepLayout({ step, title, sub, children, onSkip }) {
  const wrapperRef = useRef(null);
  const contentRef = useRef(null);
  const [measuredMinHeight, setMeasuredMinHeight] = useState(null);

  // 선택된 은행 태그처럼 실제 콘텐츠 높이가 단계별 고정 높이표보다 커질 수 있어서,
  // 스케일된 실제 렌더링 높이를 측정해 흰 카드가 그보다 작아지지 않도록 보정한다.
  useLayoutEffect(() => {
    const wrapper = wrapperRef.current;
    const content = contentRef.current;
    if (!wrapper || !content) return;

    const updateHeight = () => {
      const scale = parseFloat(getComputedStyle(content).getPropertyValue("--step-content-scale")) || 1;
      const floor = parseFloat(getComputedStyle(wrapper).getPropertyValue("--scaled-step-layout-min-height")) || 0;
      setMeasuredMinHeight(Math.max(content.offsetHeight * scale, floor));
    };

    updateHeight();
    const observer = new ResizeObserver(updateHeight);
    observer.observe(content);
    return () => observer.disconnect();
  }, [children]);

  return (
    <div
      ref={wrapperRef}
      className="relative w-full min-h-[var(--scaled-step-layout-min-height,456px)]"
      style={measuredMinHeight ? { minHeight: `${measuredMinHeight}px` } : undefined}
    >
      <div
        ref={contentRef}
        className="absolute left-0 top-0 flex min-h-[var(--step-layout-min-height,570px)] origin-top-left flex-col"
        style={{
          width: "var(--step-content-width, 125%)",
          transform: "scale(var(--step-content-scale, 0.8))",
        }}
      >
        <StepBadge step={step} />
        <div className="flex items-center justify-between">
          <h2 className="text-[30px] font-semibold text-[#454545]">{title}</h2>
          {onSkip && (
            <button
              type="button"
              onClick={onSkip}
              className="rounded-full border border-[#03BFA5] px-4 py-2 text-[18px] font-medium text-[#03BFA5] transition-colors hover:bg-[#F7FFFE]"
            >
              2단계 건너뛰기 &gt;&gt;
            </button>
          )}
        </div>
        <p className="text-[18px] font-normal text-[#454545] leading-normal whitespace-pre-line">{sub}</p>
        <div className="w-full flex flex-1 flex-col">{children}</div>
      </div>
    </div>
  );
}
