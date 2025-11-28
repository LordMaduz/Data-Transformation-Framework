package com.ruchira.murex.strategy.datafetch;

import com.ruchira.murex.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for selecting the appropriate DataFetchStrategy based on instruction event type.
 * Implements Factory pattern for dynamic strategy selection.
 *
 * <p>This factory enables the system to use different data fetching logic (queries, joins, etc.)
 * for different instruction events without modifying existing code.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataFetchStrategyFactory {

    private final List<DataFetchStrategy> strategies;

    /**
     * Get the appropriate data fetch strategy for the given instruction event
     *
     * @param instructionEvent The instruction event type (e.g., "Inception", "RolledOver")
     * @return DataFetchStrategy that supports the instruction event
     * @throws BusinessException if no strategy found for the instruction event
     */
    public DataFetchStrategy getStrategy(String instructionEvent) {
        log.debug("Looking for DataFetchStrategy for instruction event: {}", instructionEvent);

        return strategies.stream()
                .filter(strategy -> strategy.supports(instructionEvent))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        String.format("No DataFetchStrategy found for instruction event: %s", instructionEvent)
                ));
    }

    /**
     * Check if a strategy exists for the given instruction event
     *
     * @param instructionEvent The instruction event type
     * @return true if a strategy exists
     */
    public boolean hasStrategy(String instructionEvent) {
        return strategies.stream()
                .anyMatch(strategy -> strategy.supports(instructionEvent));
    }

    /**
     * Get all available data fetch strategies
     *
     * @return List of all registered strategies
     */
    public List<DataFetchStrategy> getAllStrategies() {
        return List.copyOf(strategies);
    }
}
