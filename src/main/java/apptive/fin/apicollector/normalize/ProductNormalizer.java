package apptive.fin.apicollector.normalize;

import tools.jackson.databind.JsonNode;

public interface ProductNormalizer {

    ProductDraft normalizer(String externalId, JsonNode raw);
}
