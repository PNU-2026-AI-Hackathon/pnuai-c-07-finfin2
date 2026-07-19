package apptive.fin.search.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record SearchRequestDto(
        @NotNull List<@Valid OptionRequestDto> options,
        @NotNull DetailedOptionsDto detailedOptions
) {
    // 상세옵션 널세이프 위임 접근자. detailedOptions가 없으면 null.
    public Integer age(LocalDate today) {
        return detailedOptions != null ? detailedOptions.age(today) : null;
    }

    public Integer age() {
        return detailedOptions != null ? detailedOptions.age() : null;
    }

    public Long monthlySavingsGoal() {
        return detailedOptions != null ? detailedOptions.monthlySavingsGoal() : null;
    }

    public List<String> neverUsedBanks() {
        return detailedOptions != null ? detailedOptions.neverUsedBanks() : null;
    }

    public List<String> maturedSavingBanks() {
        return detailedOptions != null ? detailedOptions.maturedSavingBanks() : null;
    }
}
