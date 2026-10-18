import { useEffect } from 'react';

export default function usePageScale(designWidth = 1400, minWidth = 960) {
  useEffect(() => {
    const update = () => {
      const clamped = Math.max(minWidth, window.innerWidth);
      const scale = Math.min(1, clamped / designWidth);
      document.documentElement.style.setProperty('--page-scale', scale);
    };

    update();
    window.addEventListener('resize', update);
    return () => window.removeEventListener('resize', update);
  }, [designWidth, minWidth]);
}