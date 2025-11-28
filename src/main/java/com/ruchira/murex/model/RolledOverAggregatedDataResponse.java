package com.ruchira.murex.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RolledOver-specific aggregated data response.
 * Currently uses only base hstg fields from {@link BaseAggregatedDataResponse}.
 *
 * <p>This class exists to:
 * 1. Provide type safety for RolledOver event processing
 * 2. Allow future addition of RolledOver-specific fields without modifying base
 * 3. Enable clear distinction between Inception and RolledOver data structures
 * </p>
 *
 * <p>RolledOver events typically need only core trade data (hstg fields) without
 * the additional allocation, entity, or historical rate information required by Inception.</p>
 *
 * <p>Future RolledOver-specific fields can be added here, for example:
 * <ul>
 *   <li>Original trade reference for rollover tracking</li>
 *   <li>Rollover-specific pricing adjustments</li>
 *   <li>Extended maturity information</li>
 * </ul>
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolledOverAggregatedDataResponse extends BaseAggregatedDataResponse {

    // Currently no additional fields beyond base class
    // Add RolledOver-specific fields here as needed

    // Example placeholders for future RolledOver-specific fields:
    // private String originalTradeReference;
    // private LocalDate originalMaturityDate;
    // private BigDecimal rolloverPriceAdjustment;
}
