package com.pipiobjo.brewery.adapters.inpot;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
public class InPotTemperatureMockAdapter implements InPotTemperatureAdapter {

    BreweryHardwareSimulation breweryHardwareSimulation = null;

    public InPotTemperatureMockAdapter(BreweryHardwareSimulation breweryHardwareSimulation){
        this.breweryHardwareSimulation = breweryHardwareSimulation;
    }

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public InpotTemperature getTemperatures() {
        InpotTemperature result = new InpotTemperature();
        result.setTimestamp(OffsetDateTime.now());

        result.setBottom(Optional.of(breweryHardwareSimulation.getInPotTempBottom()));
        result.setMiddle(Optional.of(breweryHardwareSimulation.getInPotTempMiddle()));
        result.setTop(Optional.of(breweryHardwareSimulation.getInPotTempTop()));

        return result;
    }
}
