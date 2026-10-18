package apptive.fin.apicollector.llm;

public interface LlmProviderClient {

    boolean supports(String provider);

    LlmProductEnrichment enrich(LlmProductEnrichmentRequest request);
}
