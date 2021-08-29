package com.pipiobjo.brewery.adapters.inpot;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class InPotTemperatureMockAdapter implements InPotTemperatureAdapter {

//    @Inject
//    BreweryHardwareSimulation breweryHardwareSimulation;

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public InpotTemperature getTemperatures() {
        InpotTemperature result = new InpotTemperature();
        result.setTimestamp(OffsetDateTime.now());

//        result.setBottom(Optional.of(breweryHardwareSimulation.getInPotTempBottom()));
//        result.setMiddle(Optional.of(breweryHardwareSimulation.getInPotTempMiddle()));
//        result.setTop(Optional.of(breweryHardwareSimulation.getInPotTempTop()));

        return result;
    }
}
