package com.ruchira.murex.strategy.datafetch;

import com.ruchira.murex.constant.Constants;
import com.ruchira.murex.dto.InstructionRequestDto;
import com.ruchira.murex.freemaker.FtlQueryBuilder;
import com.ruchira.murex.model.RolledOverAggregatedDataResponse;
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
 * Data fetch strategy for RolledOver instruction event.
 * Uses a separate query template (rolledOverDataFetch.ftl) with RolledOver-specific logic.
 *
 * <p>This strategy handles data fetching for RolledOver events, which only require
 * core hstg fields (no hn, ha, he fields needed like Inception).</p>
 *
 * <p>Returns {@link RolledOverAggregatedDataResponse} which includes:
 * - Base hstg fields only (core trade data)
 * - No historical exchange rate fields (hn)
 * - No allocation fields (ha)
 * - No entity fields (he)
 * </p>
 *
 * <p>Currently configured for FX Swap and NDF typologies only.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RolledOverDataFetchStrategy implements DataFetchStrategy {

    private static final String ROLLED_OVER_EVENT = "RolledOver";
    private static final String ROLLED_OVER_QUERY_TEMPLATE = "rolledOverDataFetch.ftl";

    private final GenericJdbcDataRepository repository;
    private final FtlQueryBuilder ftlQueryBuilder;

    @Override
    public boolean supports(String instructionEvent) {
        return ROLLED_OVER_EVENT.equalsIgnoreCase(instructionEvent);
    }

    @Override
    public List<RolledOverAggregatedDataResponse> fetchData(InstructionRequestDto instructionRequestDto) {
        log.info("Fetching aggregated data for RolledOver event");

        // Parse the comma-separated external trade IDs
        List<String> tradeIdList = Arrays.stream(instructionRequestDto.getExternalTradeIds().split(":"))
                .map(String::trim)
                .toList();

        // Build the SQL query using RolledOver-specific FTL template
        // TODO: Complete rolledOverDataFetch.ftl with RolledOver-specific query logic
        // Query should fetch ONLY hstg fields (no joins to hn, ha, he tables)
        Map<String, Object> inputs = Map.of(
                "businessDate", instructionRequestDto.getBusinessDate(),
                "contractList", tradeIdList,
                "typologyMx3", instructionRequestDto.getHedgeInstrumentType(),
                "inputCurrency", instructionRequestDto.getCurrency(),
                "USDCurrency", Constants.FUNCTIONAL_CURRENCY_USD,
                "tradingPortf", TRADING_PORTFOLIO_SG_BANK_SFX
                // Add RolledOver-specific parameters here
        );

        String sql = ftlQueryBuilder.buildQuery(inputs, ROLLED_OVER_QUERY_TEMPLATE);

        List<RolledOverAggregatedDataResponse> results = repository.fetchData(sql, createRowMapper());

        log.info("Fetched {} records for RolledOver event (hstg fields only)", results.size());

        return results;
    }

    @Override
    public String getInstructionEventType() {
        return ROLLED_OVER_EVENT;
    }

    /**
     * Row mapper for RolledOverAggregatedDataResponse objects.
     * Maps only base hstg fields (lighter than Inception which includes hn, ha, he).
     */
    private RowMapper<RolledOverAggregatedDataResponse> createRowMapper() {
        return new BeanPropertyRowMapper<>(RolledOverAggregatedDataResponse.class);
    }
}
