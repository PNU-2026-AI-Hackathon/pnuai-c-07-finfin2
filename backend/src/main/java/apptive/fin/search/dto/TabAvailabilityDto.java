package apptive.fin.search.dto;

import lombok.Builder;

@Builder
public record TabAvailabilityDto(
        boolean tabAEnabled,
        boolean tabBEnabled,
        String tabBDisabledReason
) {
}
