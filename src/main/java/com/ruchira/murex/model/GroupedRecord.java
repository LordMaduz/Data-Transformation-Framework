package com.ruchira.murex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a group of records sharing the same external_deal_id, comment_0, and nav_type.
 *
 * <p>Uses {@link BaseAggregatedDataResponse} to support multiple instruction events:
 * - Inception: records contain {@link InceptionAggregatedDataResponse} (base + extra fields)
 * - RolledOver: records contain {@link RolledOverAggregatedDataResponse} (base fields only)
 * - Future events: can add new response types extending base
 * </p>
 *
 * <p>This polymorphic design allows:
 * - Same GroupedRecord structure for all events
 * - TransformationContext to work uniformly
 * - Strategies to cast to specific types when needed
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupedRecord<T extends BaseAggregatedDataResponse> {

    private String contract;
    private String comment0;
    private String navType;
    private String typology;

    /**
     * List of aggregated data records in this group.
     * Actual type will be event-specific:
     * - InceptionAggregatedDataResponse for Inception events
     * - RolledOverAggregatedDataResponse for RolledOver events
     *
     * Strategies can access base fields directly or cast to specific type:
     * <pre>{@code
     * // Access base fields (works for all events)
     * String contract = record.getRecords().get(0).getContract();
     *
     * // Cast for event-specific fields (Inception only)
     * BaseAggregatedDataResponse base = record.getRecords().get(0);
     * if (base instanceof InceptionAggregatedDataResponse) {
     *     InceptionAggregatedDataResponse inception = (InceptionAggregatedDataResponse) base;
     *     BigDecimal historicalRate = inception.getHistoricalExchangeRate();
     * }
     * }</pre>
     */
    private List<T> records;
}