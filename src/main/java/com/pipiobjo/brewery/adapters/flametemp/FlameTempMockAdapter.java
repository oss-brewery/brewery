package com.pipiobjo.brewery.adapters.flametemp;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Data
public class FlameTempMockAdapter implements FlameTempAdapter{

    BreweryHardwareSimulation breweryHardwareSimulation = null;

    public FlameTempMockAdapter(BreweryHardwareSimulation breweryHardwareSimulation){
        this.breweryHardwareSimulation = breweryHardwareSimulation;
    }

    @Override
    public FlameTemperature getFlameTemp() {
        FlameTemperature result = new FlameTemperature();
        result.setTimestamp(OffsetDateTime.now());

        result.setTemperature(Optional.of(breweryHardwareSimulation.getFlameTemp()));
        return result;
    }

    @Override
    public void close() {
        log.info("close");
    }
}
