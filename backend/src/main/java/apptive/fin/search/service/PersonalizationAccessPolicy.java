package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.SearchRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PersonalizationAccessPolicy {

    /** 거래 이력은 빈 목록이면 "없음"으로 응답한 것이고, null이면 미입력한 것으로 본다. */
    public boolean canUsePersonalization(SearchRequestDto request, AuthUserDetails userDetails) {
        if (userDetails == null || request == null) {
            return false;
        }

        DetailedOptionsDto detail = request.detailedOptions();
        return detail != null
                && detail.birthdate() != null
                && detail.annualIncome() != null
                && detail.householdSize() != null
                && detail.householdIncomePercent() != null
                && request.hasTransactionHistory();
    }
}
