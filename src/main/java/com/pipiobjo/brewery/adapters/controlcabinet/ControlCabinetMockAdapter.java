package com.pipiobjo.brewery.adapters.controlcabinet;

import io.quarkus.arc.profile.IfBuildProfile;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
public class ControlCabinetMockAdapter implements ControlCabinetAdapter{
    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public ControlCabinetTemperature getTemparatures() {
        ControlCabinetTemperature result = new ControlCabinetTemperature();
        result.setTimestamp(OffsetDateTime.now());
        result.setAirTemp(Optional.of(BigDecimal.valueOf(20)));
        result.setControlCabinetAirTemp(Optional.of(BigDecimal.valueOf(21)));
        return result;
    }
}
