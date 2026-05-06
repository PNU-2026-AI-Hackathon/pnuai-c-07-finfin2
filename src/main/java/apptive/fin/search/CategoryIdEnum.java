package apptive.fin.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryIdEnum {
    REGION(1L),
    IDENTITY(2L),
    PERIOD(3L),
    BENEFIT(4L),
    INTEREST(5L),
    BANK_COND(6L);

    private final Long id;
}
