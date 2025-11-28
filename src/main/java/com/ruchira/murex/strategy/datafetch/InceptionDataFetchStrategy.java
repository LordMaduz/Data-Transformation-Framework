package com.ruchira.murex.strategy.datafetch;

import com.ruchira.murex.constant.Constants;
import com.ruchira.murex.dto.InstructionRequestDto;
import com.ruchira.murex.freemaker.FtlQueryBuilder;
import com.ruchira.murex.model.InceptionAggregatedDataResponse;
import com.ruchira.murex.repository.GenericJdbcDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ruchira.murex.constant.Constants.TRADING_PORTFOLIO_SG_BANK_SFX;

/**
 * Data fetch strategy for Inception instruction event.
 * Uses the existing aggregatedDataFetch.ftl query to maintain backward compatibility.
 *
 * <p>This strategy preserves the original Inception event data fetching logic
 * without any modifications, ensuring zero breaking changes to existing functionality.</p>
 *
 * <p>Returns {@link InceptionAggregatedDataResponse} which includes:
 * - Base hstg fields (common to all events)
 * - hn fields (historical exchange rate)
 * - ha fields (allocation data)
 * - he fields (entity information)
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InceptionDataFetchStrategy implements DataFetchStrategy {

    private static final String INCEPTION_EVENT = "Inception";
    private static final String INCEPTION_QUERY_TEMPLATE = "aggregatedDataFetch.ftl";

    private final GenericJdbcDataRepository repository;
    private final FtlQueryBuilder ftlQueryBuilder;

    @Override
    public boolean supports(String instructionEvent) {
        return INCEPTION_EVENT.equalsIgnoreCase(instructionEvent);
    }

    @Override
    public List<InceptionAggregatedDataResponse> fetchData(InstructionRequestDto instructionRequestDto) {
        log.info("Fetching aggregated data for Inception event");

        // Parse the comma-separated external trade IDs
        List<String> tradeIdList = Arrays.stream(instructionRequestDto.getExternalTradeIds().split(":"))
                .map(String::trim)
                .toList();

        // Build the SQL query using existing FTL template
        Map<String, Object> inputs = Map.of(
                "businessDate", instructionRequestDto.getBusinessDate(),
                "contractList", tradeIdList,
                "typologyMx3", instructionRequestDto.getHedgeInstrumentType(),
                "inputCurrency", instructionRequestDto.getCurrency(),
                "USDCurrency", Constants.FUNCTIONAL_CURRENCY_USD,
                "tradingPortf", TRADING_PORTFOLIO_SG_BANK_SFX
        );

        String sql = ftlQueryBuilder.buildQuery(inputs, INCEPTION_QUERY_TEMPLATE);

        List<InceptionAggregatedDataResponse> results = repository.fetchData(sql, createRowMapper());

        log.info("Fetched {} records for Inception event", results.size());

        return results;
    }

    @Override
    public String getInstructionEventType() {
        return INCEPTION_EVENT;
    }

    /**
     * Row mapper for InceptionAggregatedDataResponse objects
     * Maps all fields from base (hstg) + hn + ha + he tables
     */
    private RowMapper<InceptionAggregatedDataResponse> createRowMapper() {
        return new BeanPropertyRowMapper<>(InceptionAggregatedDataResponse.class);
    }
}
