package apptive.fin.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InterestRateType {

   SINGLE_INTEREST("단리"),
   COMPOUND_INTEREST("복리");

   private final String value;
}
