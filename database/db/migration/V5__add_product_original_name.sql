-- 상품 원본 제목(original_name)과 디스플레이 제목(product_name)을 분리한다.
-- product_name은 괄호가 제거된 디스플레이 이름으로 계속 사용하고, original_name에 원본을 보관한다.
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS original_name VARCHAR(200);

-- 기존 FSS 행은 이미 괄호가 제거되어 참 원본을 복원할 수 없으므로 현재 product_name으로 백필한다.
-- 다음 수집(재정규화) 실행에서 original_name이 참 원본으로 교정된다.
UPDATE product
    SET original_name = product_name
    WHERE original_name IS NULL;
