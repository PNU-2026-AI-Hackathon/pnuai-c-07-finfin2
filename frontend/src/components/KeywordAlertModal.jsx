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
      className="fixed inset-0 z-100 flex items-start justify-center bg-[#989898]/53 px-4 pt-[33.5vh]"
      role="presentation"
      onMouseDown={onClose}
    >
      <div
        className="flex h-[397px] w-full max-w-[544px] flex-col items-center rounded-[20px] bg-white pt-[76px] text-center"
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="keyword-alert-title"
        aria-describedby="keyword-alert-description"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mb-[25px] flex size-[69px] items-center justify-center rounded-full border-2 border-[#03BFA5] text-[#03BFA5]">
          <svg
            className="size-[39px]"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.7"
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
          className="text-[24px] font-semibold leading-[1.22] text-[#03BFA5]"
        >
          키워드를 선택해주세요
        </h2>
        <p
          id="keyword-alert-description"
          className="mt-[25px] text-[15px] leading-[1.22] text-[#03BFA5]"
        >
          1개 이상의 키워드를 선택해야 다음 단계로 이동할 수 있어요.
        </p>

        <button
          type="button"
          autoFocus
          onClick={onClose}
          className="mt-10 h-10 w-[228px] rounded-[5px] border border-[#E0DFDF] text-[15px] leading-[1.22] text-[#4B4B4B] transition-colors hover:border-[#03BFA5] hover:text-[#03BFA5]"
        >
          확인
        </button>
      </div>
    </div>
  );
}
