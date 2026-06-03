package apptive.fin.search.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SearchRequestDto(
        @NotNull List<@Valid OptionRequestDto> options,
        @NotNull DetailedOptionsDto detailedOptions
) {
}
