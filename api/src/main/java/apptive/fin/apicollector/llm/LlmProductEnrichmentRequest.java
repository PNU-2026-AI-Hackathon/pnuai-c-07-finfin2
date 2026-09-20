package apptive.fin.apicollector.llm;

public record LlmProductEnrichmentRequest(
        String model,
        String prompt,
        int schemaVersion
) {
}
