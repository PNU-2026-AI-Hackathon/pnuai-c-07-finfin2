package apptive.fin.term.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserTermRequestDto(@Valid List<TermAgreement> agreements) {
    public record TermAgreement(
            @NotNull Long versionId,
            @NotNull Boolean agreed
    ) {}
}