-- V6: 저축기간 옵션 확장 (PRD 개정: 대분류 라우팅 구조)
-- 기존 범위형 옵션은 하위 호환을 위해 유지하고, 정확한 개월 수 옵션을 신규 추가
-- 단기예치: 1개월, 3개월 / 목돈만들기: 6개월, 1년, 2년, 3년

-- 신규 저축기간 옵션 삽입 (기존 옵션 유지, 신규 옵션 추가)
INSERT INTO category_option (category_id, value, code) VALUES
(3, '1개월', 'TERM_1_MONTH'),
(3, '3개월', 'TERM_3_MONTH'),
(3, '6개월', 'TERM_6_MONTH'),
(3, '1년', 'TERM_12_MONTH'),
(3, '2년', 'TERM_24_MONTH'),
(3, '3년', 'TERM_36_MONTH');
