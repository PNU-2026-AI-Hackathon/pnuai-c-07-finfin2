package apptive.fin.search.enums;

import java.math.BigDecimal;

public enum TaxType {
    GENERAL(new BigDecimal("0.154")), // 일반 15.4%
    NON_TAX(BigDecimal.ZERO);          // 비과세 0%

    private final BigDecimal rate;

    TaxType(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
