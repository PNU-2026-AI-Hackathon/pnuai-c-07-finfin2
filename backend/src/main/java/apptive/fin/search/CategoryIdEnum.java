package apptive.fin.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

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

    public static Optional<CategoryIdEnum> fromId(Long id) {
        for (CategoryIdEnum categoryIdEnum : CategoryIdEnum.values()) {
            if (categoryIdEnum.getId().equals(id)) {
                return Optional.of(categoryIdEnum);
            }
        }
        return Optional.empty();
    }
}
