package apptive.fin.search;

import apptive.fin.search.entity.ProductProperty;

public final class ProductAvailability {

    private ProductAvailability() {
    }

    public static boolean isJoinable(ProductProperty property) {
        return property != null && property.isJoinable();
    }

    public static ProductApplyStatus applyStatus(ProductProperty property) {
        return isJoinable(property)
                ? ProductApplyStatus.AVAILABLE
                : ProductApplyStatus.RECRUIT_CLOSED;
    }

    public static String applyUrl(ProductProperty property) {
        return isJoinable(property) ? property.resolvedApplyUrl() : null;
    }
}
