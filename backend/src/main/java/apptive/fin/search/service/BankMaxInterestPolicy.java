package apptive.fin.search.service;

import apptive.fin.search.entity.ProductProperty;

import java.util.Comparator;
import java.util.List;

public final class BankMaxInterestPolicy {

    private BankMaxInterestPolicy() {
    }

    public static Double calculateThreshold(List<Double> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }

        List<Double> sortedDesc = rates.stream()
                .sorted(Comparator.reverseOrder())
                .toList();
        int cutoffIndex = (int) Math.ceil(sortedDesc.size() * 0.3) - 1;
        return sortedDesc.get(Math.max(cutoffIndex, 0));
    }

    public static boolean qualifies(ProductProperty property, Double threshold) {
        return threshold != null
                && property.getMaxRate() != null
                && property.getMaxRate().doubleValue() >= threshold;
    }

    public static boolean anyJoinableQualifies(List<ProductProperty> properties, Double threshold) {
        return properties.stream()
                .filter(ProductProperty::isJoinable)
                .anyMatch(property -> qualifies(property, threshold));
    }
}
