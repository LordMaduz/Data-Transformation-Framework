package com.ruchira.murex.mapper;

import com.ruchira.murex.dto.StgMrxExtDmcDto;
import com.ruchira.murex.model.BaseAggregatedDataResponse;
import com.ruchira.murex.model.TransformedMurexTrade;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Map;

/**
 * Dynamic mapper for transforming aggregated data responses to various DTOs.
 * Works with BaseAggregatedDataResponse to support all instruction event types.
 */
@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface DynamicMapper {

    StgMrxExtDmcDto mapToDmcDto(TransformedMurexTrade dataResponse);

    /**
     * Maps aggregated data to Murex trade leg with field overrides.
     * Works with all event types (Inception, RolledOver) using base fields.
     */
    TransformedMurexTrade mapToMurexTradeLeg(BaseAggregatedDataResponse dataResponse, @Context Map<String, Object> overrides);

    /**
     * Maps aggregated data to Murex trade leg.
     * Works with all event types (Inception, RolledOver) using base fields.
     */
    TransformedMurexTrade mapToMurexTradeLeg(BaseAggregatedDataResponse dataResponse);

    TransformedMurexTrade clone(TransformedMurexTrade transformedMurexTrade);

    @AfterMapping
    default <S, T> void overrideValues(S source, @MappingTarget T target, @Context Map<String, Object> overrides) {
        Logger log = LoggerFactory.getLogger(DynamicMapper.class);

        if (ObjectUtils.isNotEmpty(overrides)) {
            BeanWrapper wrapper = new BeanWrapperImpl(target);
            overrides.forEach((key, value) -> {
                if (wrapper.isWritableProperty(key)) {
                    wrapper.setPropertyValue(key, value);
                    log.debug("Overridden property '{}' with value '{}' on {}", key, value, target.getClass().getSimpleName());
                } else {
                    log.debug("Skipping override: property '{}' not found on {}", key, target.getClass().getSimpleName());
                }
            });
        }
    }

}
