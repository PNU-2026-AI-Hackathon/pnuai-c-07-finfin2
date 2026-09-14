package apptive.fin.search.service;

import apptive.fin.search.entity.ProductProperty;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BankMaxInterestPolicyTest {

    @Test
    void 상위30퍼센트_컷금리는_5개중_두번째로_높은_금리다() {
        Double threshold = BankMaxInterestPolicy.calculateThreshold(
                List.of(1.0, 2.0, 3.0, 4.0, 5.0)
        );

        assertThat(threshold).isEqualTo(4.0);
    }

    @Test
    void 컷에_동점이_있어도_같은_임계값을_반환한다() {
        Double threshold = BankMaxInterestPolicy.calculateThreshold(
                List.of(2.0, 3.0, 4.0, 4.0, 5.0)
        );

        assertThat(threshold).isEqualTo(4.0);
    }

    @Test
    void 후보금리가_없으면_임계값이_없다() {
        assertThat(BankMaxInterestPolicy.calculateThreshold(List.of())).isNull();
        assertThat(BankMaxInterestPolicy.calculateThreshold(null)).isNull();
    }

    @Test
    void 후보금리가_하나면_그_값이_임계값이다() {
        assertThat(BankMaxInterestPolicy.calculateThreshold(List.of(3.5))).isEqualTo(3.5);
    }

    @Test
    void 옵션금리가_임계값과_같으면_기준을_충족한다() {
        ProductProperty property = property("4.00", true);

        boolean qualifies = BankMaxInterestPolicy.qualifies(property, 4.0);

        assertThat(qualifies).isTrue();
    }

    @Test
    void 옵션금리가_임계값보다_낮으면_기준을_충족하지_않는다() {
        ProductProperty property = property("3.99", true);

        boolean qualifies = BankMaxInterestPolicy.qualifies(property, 4.0);

        assertThat(qualifies).isFalse();
    }

    @Test
    void 옵션금리나_임계값이_없으면_기준을_충족하지_않는다() {
        ProductProperty propertyWithoutRate = property(null, true);

        assertThat(List.of(
                BankMaxInterestPolicy.qualifies(propertyWithoutRate, 4.0),
                BankMaxInterestPolicy.qualifies(property("4.00", true), null)
        )).containsExactly(false, false);
    }

    @Test
    void 비활성옵션은_임계값_이상이어도_상품판정에서_제외한다() {
        List<ProductProperty> properties = List.of(property("5.00", false));

        boolean qualifies = BankMaxInterestPolicy.anyJoinableQualifies(properties, 4.0);

        assertThat(qualifies).isFalse();
    }

    @Test
    void 가입가능옵션중_하나라도_임계값을_충족하면_상품이_기준을_충족한다() {
        List<ProductProperty> properties = List.of(
                property("3.00", true),
                property("4.00", true)
        );

        boolean qualifies = BankMaxInterestPolicy.anyJoinableQualifies(properties, 4.0);

        assertThat(qualifies).isTrue();
    }

    private ProductProperty property(String maxRate, boolean joinable) {
        ProductProperty property = new ProductProperty();
        ReflectionTestUtils.setField(
                property,
                "maxRate",
                maxRate != null ? new BigDecimal(maxRate) : null
        );
        ReflectionTestUtils.setField(property, "isJoinable", joinable);
        return property;
    }
}
