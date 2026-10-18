package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.raw.ProductRaw;

public interface ProductDraftEnricher {

    boolean supports(Source source);

    ProductDraft enrich(ProductRaw rawProduct, ProductDraft draft);
}
