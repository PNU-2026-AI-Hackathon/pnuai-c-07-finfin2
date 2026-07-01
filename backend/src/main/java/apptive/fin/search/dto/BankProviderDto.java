package apptive.fin.search.dto;

public record BankProviderDto(
        String code,
        String name,
        String category,
        String region
) {
}
