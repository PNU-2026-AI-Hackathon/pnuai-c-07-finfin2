package apptive.fin.search.dto;

import jakarta.validation.constraints.NotNull;

public record OptionRequestDto(
        @NotNull Long categoryId,
        @NotNull Long optionId
) {
}
