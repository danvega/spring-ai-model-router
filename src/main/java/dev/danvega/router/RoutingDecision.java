package dev.danvega.router;

import java.util.Map;

public record RoutingDecision(ModelTier tier, String model, double confidence, Map<String, Double> probabilities) {
}
