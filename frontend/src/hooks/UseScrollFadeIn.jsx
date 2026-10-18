import { useEffect, useRef } from 'react';

export default function useScrollFadeIn() {
  const ref = useRef(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (!entry.isIntersecting) return;

        // 뷰포트 진입 후 300ms 대기 후 시작, 타이틀 먼저
        setTimeout(() => {
          const title = el.querySelector('[data-fade="title"]');
          if (title) {
            title.classList.add('opacity-100', 'translate-y-0');
            title.classList.remove('opacity-0', 'translate-y-6');
          }

          // 하위 아이템 순차적으로
          const items = el.querySelectorAll('[data-fade="item"]');
          items.forEach((item, i) => {
            setTimeout(() => {
              item.classList.add('opacity-100', 'translate-y-0');
              item.classList.remove('opacity-0', 'translate-y-6');
            }, 200 + i * 120);
          });
        }, 300);

        observer.disconnect();
      },
      { threshold: 0.2 }
    );

    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return ref;
}