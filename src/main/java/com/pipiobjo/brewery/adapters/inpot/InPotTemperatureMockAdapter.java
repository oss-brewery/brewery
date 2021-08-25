package com.pipiobjo.brewery.adapters.inpot;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class InPotTemperatureMockAdapter implements InPotTemperatureAdapter {

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public InpotTemperature getTemperatures() {
        InpotTemperature result = new InpotTemperature();
        result.setTimestamp(OffsetDateTime.now());

        result.setBottom(Optional.of(BreweryHardwareSimulation.getInPotTempBottom()));
        result.setMiddle(Optional.of(BreweryHardwareSimulation.getInPotTempMiddle()));
        result.setTop(Optional.of(BreweryHardwareSimulation.getInPotTempTop()));

        return result;
    }
}
