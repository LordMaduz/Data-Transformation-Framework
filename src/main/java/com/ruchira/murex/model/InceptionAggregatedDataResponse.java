package com.ruchira.murex.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Inception-specific aggregated data response.
 * Extends base response with additional fields from hn, ha, and he tables.
 *
 * <p>This class is used exclusively for Inception instruction events, which require
 * additional data beyond the core hstg fields (historical exchange rates, allocation data,
 * entity information).</p>
 *
 * <p>Field Categories:
 * - Base fields (inherited): All hstg table columns
 * - hn fields: Historical exchange rate data (h_nav table)
 * - ha fields: Allocation data (h_allocation table)
 * - he fields: Entity data (h_entity table)
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InceptionAggregatedDataResponse extends BaseAggregatedDataResponse {

    // --- hn fields (h_nav table) ---
    // Historical exchange rate information specific to Inception
    private BigDecimal historicalExchangeRate;

    // --- ha fields (h_allocation table) ---
    // Allocation and exposure data specific to Inception
    private String navType;
    private String exposureCurrency;
    private String apportionmentCurrency;
    private BigDecimal hedgeAmtAllocation;
    private String entityName;

    // --- he fields (h_entity table) ---
    // Entity information specific to Inception
    private String entityType;
    private String entityId;
    private String murexComment;
}
