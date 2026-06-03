DELETE FROM product_property_keyword;
DELETE FROM product_properties;
DELETE FROM product;
DELETE FROM provider;

INSERT INTO provider (source_id, code, name) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'TEST_BANK', '테스트은행'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'TEST_GOV', '테스트정책기관');

INSERT INTO product (source_id, type, product_code, product_name, content) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'TEST_COMMON', '공통 노출 상품', '기본 조건 상품'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'TEST_TENURE_REQUIRED', '근속기간 요구 상품', '근속기간 조건 상품'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'TEST_HOMELESS_ONLY', '무주택자 전용 상품', '무주택 조건 상품'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'TEST_YOUTH_PREFERENTIAL', '청년 우대형 상품', '만 29세 이하 상품');

INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
    min_age, max_age, min_tenure_months, requires_homeless, requires_householder,
    is_joinable, intr_rate_type, save_trm
) VALUES
((SELECT id FROM product WHERE product_code = 'TEST_COMMON'), (SELECT id FROM provider WHERE code = 'TEST_BANK'), 3.00, 3.50, 10, 100, 19, 34, NULL, false, false, true, 'SINGLE_INTEREST', 12),
((SELECT id FROM product WHERE product_code = 'TEST_TENURE_REQUIRED'), (SELECT id FROM provider WHERE code = 'TEST_GOV'), 4.00, 4.00, 10, 100, 19, 34, 6, false, false, true, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'TEST_HOMELESS_ONLY'), (SELECT id FROM provider WHERE code = 'TEST_GOV'), 4.50, 6.00, 10, 100, 19, 34, NULL, true, false, true, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'TEST_YOUTH_PREFERENTIAL'), (SELECT id FROM provider WHERE code = 'TEST_BANK'), 3.80, 4.50, 10, 100, 19, 29, NULL, false, false, true, 'SINGLE_INTEREST', 12);
