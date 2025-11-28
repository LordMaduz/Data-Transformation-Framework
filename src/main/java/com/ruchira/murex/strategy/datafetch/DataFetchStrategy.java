package com.ruchira.murex.strategy.datafetch;

import com.ruchira.murex.dto.InstructionRequestDto;
import com.ruchira.murex.model.BaseAggregatedDataResponse;

import java.util.List;

/**
 * Strategy interface for fetching aggregated data based on instruction event type.
 * Each instruction event (Inception, RolledOver, etc.) may require different queries,
 * joins, and data retrieval logic.
 *
 * <p>Implementation classes should be Spring @Component beans to enable auto-discovery
 * by the DataFetchStrategyFactory.</p>
 *
 * <p>Implementations return event-specific response types:
 * - InceptionDataFetchStrategy returns {@link com.ruchira.murex.model.InceptionAggregatedDataResponse}
 * - RolledOverDataFetchStrategy returns {@link com.ruchira.murex.model.RolledOverAggregatedDataResponse}
 * All types extend {@link BaseAggregatedDataResponse} for polymorphism.
 * </p>
 */
public interface DataFetchStrategy {

    /**
     * Check if this strategy supports the given instruction event type
     *
     * @param instructionEvent The instruction event type (e.g., "Inception", "RolledOver")
     * @return true if this strategy can handle the instruction event
     */
    boolean supports(String instructionEvent);

    /**
     * Fetch aggregated data for the given instruction request.
     * Returns event-specific response type (InceptionAggregatedDataResponse or RolledOverAggregatedDataResponse).
     *
     * @param instructionRequestDto The instruction request containing all filter criteria
     * @return List of event-specific aggregated data responses extending BaseAggregatedDataResponse
     */
    List<? extends BaseAggregatedDataResponse> fetchData(InstructionRequestDto instructionRequestDto);

    /**
     * Get the instruction event type this strategy handles
     *
     * @return The instruction event type name
     */
    String getInstructionEventType();
}
