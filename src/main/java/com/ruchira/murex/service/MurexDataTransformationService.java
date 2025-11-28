package com.ruchira.murex.service;

import com.ruchira.murex.dto.StgMrxExtDmcDto;
import com.ruchira.murex.model.RecordProcessingResult;
import com.ruchira.murex.model.trade.MurexTrade;
import com.ruchira.murex.model.TransformationContext;
import com.ruchira.murex.strategy.TransformationStrategyFactory;
import com.ruchira.murex.strategy.TransformationStrategy;
import com.ruchira.murex.exception.TransformationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Refactored service for Murex booking transformation logic
 * Uses Strategy pattern for extensible transformation handling
 */
@Service
@RequiredArgsConstructor
public class MurexDataTransformationService {

    private final TransformationStrategyFactory strategyFactory;

    /**
     * Generate MurexBookingDTO objects using strategy pattern.
     * Uses event-aware strategy selection based on instruction event + typology combination.
     *
     * @param transformationContext Context containing the instruction event, grouped record,
     *                              filtered Murex configurations, input currency, and all grouped records
     * @return RecordProcessingResult containing generated DTOs and trades
     */
    @Transactional
    public RecordProcessingResult generateMurexBookings(TransformationContext transformationContext) {

        String instructionEvent = transformationContext.getInstructionEvent();
        String typology = transformationContext.getGroupedRecord().getTypology();

        try {
            // Use event-aware strategy selection
            TransformationStrategy strategy = strategyFactory.getStrategy(instructionEvent, typology);
            return strategy.process(transformationContext);

        } catch (Exception e) {
            throw new TransformationException(
                    String.format("Failed to generate Murex bookings for instruction event: %s, typology: %s",
                            instructionEvent, typology),
                    typology,
                    e
            );
        }
    }
}