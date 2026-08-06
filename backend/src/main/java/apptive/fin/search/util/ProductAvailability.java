package apptive.fin.search.util;

import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.enums.ProductApplyStatus;

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
