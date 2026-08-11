package apptive.fin.search.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    // 금감원이 이름 끝 괄호로 붙여 내려주는 적립·지급 방식, 상품유형, 시즌 표기는 표시할 때 뗀다.
    // 수집기는 이 괄호를 지우지 않는다 — 은행 URL 스크래퍼가 상품을 구분하는 근거이기 때문이다.
    @ParameterizedTest
    @CsvSource({
            "'Sh해양플라스틱Zero!적금 (정액적립식)', 'Sh해양플라스틱Zero!적금'",
            "'Sh해양플라스틱Zero!예금 (만기일시지급식)', 'Sh해양플라스틱Zero!예금'",
            "'제주Dream 정기예금 (개인/만기 지급식)', '제주Dream 정기예금'",
            "'JB 다이렉트적금(자유적립식)', 'JB 다이렉트적금'",
            "'KB국민프리미엄적금(정액)', 'KB국민프리미엄적금'",
            "'The든든예금(시즌2)', 'The든든예금'",
            "'IBK평생한가족통장(실세금리정기예금)', 'IBK평생한가족통장'"
    })
    void stripsTrailingParen(String stored, String displayed) {
        assertThat(Product.stripTrailingParen(stored)).isEqualTo(displayed);
    }

    // 이름 중간에 박힌 브랜드 괄호는 끝이 아니므로 남는다.
    @ParameterizedTest
    @CsvSource({
            "'헤이(Hey)적금 (자유적립식)', '헤이(Hey)적금'",
            "'더(The) 특판 정기예금', '더(The) 특판 정기예금'",
            "'헤이(Hey)정기예금', '헤이(Hey)정기예금'"
    })
    void keepsBrandParenInTheMiddle(String stored, String displayed) {
        assertThat(Product.stripTrailingParen(stored)).isEqualTo(displayed);
    }

    @Test
    void handlesNullName() {
        assertThat(Product.stripTrailingParen(null)).isNull();
    }

    @Test
    void displayNameStripsTrailingParenFromStoredName() {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "productName", "JB 다이렉트적금(자유적립식)");

        // 저장된 이름은 원천 그대로, 응답용 이름만 가공된다.
        assertThat(product.getProductName()).isEqualTo("JB 다이렉트적금(자유적립식)");
        assertThat(product.getDisplayProductName()).isEqualTo("JB 다이렉트적금");
    }
}
