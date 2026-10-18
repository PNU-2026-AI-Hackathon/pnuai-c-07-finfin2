package apptive.fin.apicollector.client.fss;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FssFinancialGroup {

    BANK("020000", "은행"),
//    CREDIT_FINANCE("030200", "여신전문금융"),
//    SAVINGS_BANK("030300", "저축은행"),
//    INSURANCE("050000", "보험"),
//    FINANCIAL_INVESTMENT("060000", "금융투자")
    ;

    private final String code;
    private final String description;
}
