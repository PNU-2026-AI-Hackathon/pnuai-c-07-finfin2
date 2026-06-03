DELETE FROM product_property_keyword;
DELETE FROM product_properties;
DELETE FROM product;
DELETE FROM provider;

INSERT INTO provider (source_id, code, name) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'SEARCH_BANK_A', '더케이저축은행'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'SEARCH_GOV', '금융위원회'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SEARCH_BANK_B', '국민은행');

INSERT INTO product (source_id, type, product_code, product_name, content) VALUES
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'SEARCH_SAFE_DEPOSIT', 'e-쎄이프 정기예금', '단리/복리 선택 가능'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'SEARCH_YOUTH_EMPLOYMENT', '청년내일채움공제', '중소기업 재직 청년 자산형성 지원'),
((SELECT id FROM product_source WHERE code = 'ONTONG'), 'POLICY', 'SEARCH_SUBSCRIPTION', '청년우대형 청약통장', '청년 자산형성 주거 상품'),
((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', 'SEARCH_YOUTH_SAVING', '청년우대적금', '만 19~29세 전용 우대 적금');

INSERT INTO product_properties (
    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
    min_age, max_age, min_tenure_months, requires_homeless, requires_householder,
    is_joinable, intr_rate_type, save_trm
) VALUES
((SELECT id FROM product WHERE product_code = 'SEARCH_SAFE_DEPOSIT'), (SELECT id FROM provider WHERE code = 'SEARCH_BANK_A'), 3.45, 3.45, 10, 100, 19, 34, NULL, false, false, true, 'SINGLE_INTEREST', 12),
((SELECT id FROM product WHERE product_code = 'SEARCH_YOUTH_EMPLOYMENT'), (SELECT id FROM provider WHERE code = 'SEARCH_GOV'), 10.0, 10.0, 12, 50, 15, 34, 6, false, false, true, NULL, 24),
((SELECT id FROM product WHERE product_code = 'SEARCH_SUBSCRIPTION'), (SELECT id FROM provider WHERE code = 'SEARCH_GOV'), 4.5, 6.0, 1, 70, 19, 34, NULL, true, false, true, NULL, NULL),
((SELECT id FROM product WHERE product_code = 'SEARCH_YOUTH_SAVING'), (SELECT id FROM provider WHERE code = 'SEARCH_BANK_B'), 3.8, 4.5, 10, 50, 19, 29, NULL, false, false, true, 'SINGLE_INTEREST', 12);

INSERT INTO product_property_keyword (product_property_id, keyword_code) VALUES
((SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'SEARCH_SUBSCRIPTION'), 'INTEREST_SAVINGS'),
((SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'SEARCH_YOUTH_SAVING'), 'STATUS_MILITARY'),
((SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = 'SEARCH_YOUTH_SAVING'), 'REGION_BUSAN');
