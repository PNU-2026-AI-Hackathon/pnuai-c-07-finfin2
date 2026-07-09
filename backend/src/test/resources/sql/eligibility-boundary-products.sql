DELETE FROM product_property_keyword;
DELETE FROM product_property_required_keyword;
DELETE FROM product_properties;
DELETE FROM product;
DELETE FROM provider;

INSERT INTO provider (source_id, code, name) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'TEST_BANK', '테스트은행'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'TEST_GOV', '테스트정책기관');

INSERT INTO product (source_id, type, product_code, product_name, content) VALUES
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'TEST_AGE_MIL_EXT', '병역 연령 확장 인정 상품', '만 34세 이하, 병역 연령 확장 인정'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'TEST_AGE_NO_MIL_EXT', '병역 연령 확장 미인정 상품', '만 34세 이하, 병역 연령 확장 미인정'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'TEST_INCOME_MAX', '소득 상한 상품', '연소득 2400만원 이하'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'TEST_INCOME_NONE', '소득요건 없는 상품', '소득 조건 없음'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'TEST_INCOME_PERCENT', '중위소득 상한 상품', '가구 중위소득 120% 이하'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'TEST_MONTHLY_MIN', '최소납입액 상품', '월 최소 10만원 납입');

INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
    min_age, max_age, allows_military_age_extension, military_max_age,
    earn_max_amt, earn_percent,
    min_tenure_months, requires_homeless, requires_householder,
    is_joinable, intr_rate_type, save_trm
) VALUES
((SELECT id FROM product WHERE product_code = 'TEST_AGE_MIL_EXT'), (SELECT id FROM provider WHERE code = 'TEST_GOV'), 4.00, 4.00, 10, 100, 19, 34, true, 39, NULL, NULL, NULL, false, false, true, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'TEST_AGE_NO_MIL_EXT'), (SELECT id FROM provider WHERE code = 'TEST_GOV'), 4.00, 4.00, 10, 100, 19, 34, false, NULL, NULL, NULL, NULL, false, false, true, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'TEST_INCOME_MAX'), (SELECT id FROM provider WHERE code = 'TEST_BANK'), 3.00, 3.50, 10, 100, 19, 99, false, NULL, 24000000, NULL, NULL, false, false, true, 'SINGLE_INTEREST', 12),
((SELECT id FROM product WHERE product_code = 'TEST_INCOME_NONE'), (SELECT id FROM provider WHERE code = 'TEST_BANK'), 3.00, 3.50, 10, 100, 19, 99, false, NULL, NULL, NULL, NULL, false, false, true, 'SINGLE_INTEREST', 12),
((SELECT id FROM product WHERE product_code = 'TEST_INCOME_PERCENT'), (SELECT id FROM provider WHERE code = 'TEST_GOV'), 4.00, 4.00, 10, 100, 19, 99, false, NULL, NULL, 120, NULL, false, false, true, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'TEST_MONTHLY_MIN'), (SELECT id FROM provider WHERE code = 'TEST_BANK'), 3.00, 3.50, 100000, 1000000, 19, 99, false, NULL, NULL, NULL, NULL, false, false, true, 'SINGLE_INTEREST', 12);
