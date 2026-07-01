package apptive.fin.provider;

public record BankDisplayInfo(
        String code,
        String displayName,
        String category,
        String region
) {
}
