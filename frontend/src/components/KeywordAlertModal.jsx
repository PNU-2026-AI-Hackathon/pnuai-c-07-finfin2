import { useEffect } from "react";

export default function KeywordAlertModal({ onClose }) {
  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.key === "Escape") onClose();
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [onClose]);

  return (
    <div
      className="fixed inset-0 z-100 flex items-center justify-center bg-black/25 px-4"
      role="presentation"
      onMouseDown={onClose}
    >
      <div
        className="w-full max-w-[360px] rounded-[20px] bg-white px-10 py-10 text-center shadow-2xl"
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="keyword-alert-title"
        aria-describedby="keyword-alert-description"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mx-auto mb-5 flex size-12 items-center justify-center rounded-full border-2 border-[#03BFA5] text-[#03BFA5]">
          <svg
            className="size-6"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            aria-hidden="true"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M10.3 4.5 3.4 16.4A2 2 0 0 0 5.1 19h13.8a2 2 0 0 0 1.7-2.6L13.7 4.5a2 2 0 0 0-3.4 0Z"
            />
            <path strokeLinecap="round" d="M12 9v4m0 2.5h.01" />
          </svg>
        </div>

        <h2
          id="keyword-alert-title"
          className="text-[18px] font-bold text-[#03BFA5]"
        >
          키워드를 1개 이상 선택해주세요
        </h2>
        <p
          id="keyword-alert-description"
          className="mt-3 text-[12px] text-[#03BFA5]"
        >
          1개 이상의 키워드를 선택해야 다음 단계로 이동할 수 있어요.
        </p>

        <button
          type="button"
          autoFocus
          onClick={onClose}
          className="mt-7 h-9 w-full rounded-[4px] border border-[#E2E2E2] text-[13px] text-[#454545] transition-colors hover:border-[#03BFA5] hover:text-[#03BFA5]"
        >
          확인
        </button>
      </div>
    </div>
  );
}
