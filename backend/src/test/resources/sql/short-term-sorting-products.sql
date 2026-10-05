-- 단기예치 정렬 테스트용 상품 데이터
DELETE FROM product_property_keyword;
DELETE FROM product_property_required_keyword;
DELETE FROM product_properties;
DELETE FROM product;
DELETE FROM provider;

-- 테스트용 은행 제공자
INSERT INTO provider (source_id, code, name) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'SHORT_BANK_A', '테스트은행A'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SHORT_BANK_B', '테스트은행B'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SHORT_BANK_C', '테스트은행C');

-- 단기예치 예적금 상품 (1개월, 서로 다른 금리)
INSERT INTO product (source_id, type, product_code, product_name, content) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'SHORT_HIGH_RATE', '고금리예금', '높은 금리 상품'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'SHORT_MID_RATE', '중금리예금', '중간 금리 상품'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'DEPOSIT', 'SHORT_LOW_RATE', '저금리예금', '낮은 금리 상품');

-- 상품 속성: 다른 금리로 정렬 테스트
-- 정렬 기대: 고금리(4.5%) > 중금리(3.5%) > 저금리(2.5%)
INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
    min_age, max_age, is_joinable, intr_rate_type, save_trm
) VALUES
((SELECT id FROM product WHERE product_code = 'SHORT_HIGH_RATE'), (SELECT id FROM provider WHERE code = 'SHORT_BANK_A'), 4.0, 4.5, NULL, NULL, 17, 100, true, 'SINGLE_INTEREST', 1),
((SELECT id FROM product WHERE product_code = 'SHORT_MID_RATE'), (SELECT id FROM provider WHERE code = 'SHORT_BANK_B'), 3.0, 3.5, NULL, NULL, 17, 100, true, 'SINGLE_INTEREST', 1),
((SELECT id FROM product WHERE product_code = 'SHORT_LOW_RATE'), (SELECT id FROM provider WHERE code = 'SHORT_BANK_C'), 2.0, 2.5, NULL, NULL, 17, 100, true, 'SINGLE_INTEREST', 1);

-- 우대조건 키워드 (예적금 탭 활성화용)
INSERT INTO product_property_keyword (product_property_id, keyword_code) VALUES
((SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'SHORT_HIGH_RATE'), 'BANK_SALARY_TRANSFER'),
((SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'SHORT_MID_RATE'), 'BANK_SALARY_TRANSFER'),
((SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'SHORT_LOW_RATE'), 'BANK_SALARY_TRANSFER');
