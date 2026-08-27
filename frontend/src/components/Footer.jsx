import React from 'react';

export default function Footer() {
  return (
    <footer className="font-pretendard bg-[#94AFAC] py-16 px-6 sm:px-10">
      <div className="max-w-6xl mx-auto text-white">
        
        {}
        <div className="flex flex-col gap-4 mb-10 border-b border-white/20 pb-10">
          <button className="w-fit hover:underline text-lg font-medium">
            Y-Fin. 을 만드는 사람들
          </button>
          <div className="flex flex-wrap gap-x-8 gap-y-2 text-sm opacity-90">
            <p>문의 | 고객지원</p>
          </div>
        </div>

        {}
        <div className="space-y-4 text-[12px] leading-relaxed opacity-80 font-light">
          <p>
            본 서비스는 금융소비자보호법상의 '금융상품판매대리·중개업'에 해당하지 않습니다.
          </p>
          <p>
            Y-Fin.은 금융상품 계약 체결권을 가지지 않으며, 단지 정보 제공 및 자격 자가진단 도구만을 제공합니다. 
            회원이 수동으로 입력한 데이터의 정확성을 전제로 결과를 산출하며, 이용자가 입력한 정보의 오류, 허위 기재 또는 누락으로 인해 발생한 결과에 대하여 Y-Fin.은 어떠한 법적 책임도 지지 않습니다.
          </p>
          <p>
            Y-Fin.이 제공하는 적합도 점수 및 매칭률은 단순 참고용 시뮬레이션 결과입니다. 이는 실제 금융기관의 대출 승인, 최종 확정 이율, 가입 가능 여부를 결정하거나 법적으로 보장하지 않습니다. 
            금융상품의 계약 당사자가 아니며, 회원이 개별 금융기관과 체결하는 계약 내용, 서비스 품질 및 그로 인해 발생하는 분쟁에 대하여 개입하거나 책임지지 않습니다.
          </p>
          <p className="pt-4 opacity-60">
            © 2026 Y-Fin. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}