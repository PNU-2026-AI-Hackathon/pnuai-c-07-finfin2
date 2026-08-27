package apptive.fin.search.dto;

import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;

public record EligibleProductOption(
        Product product,
        ProductProperty property
) {
}
