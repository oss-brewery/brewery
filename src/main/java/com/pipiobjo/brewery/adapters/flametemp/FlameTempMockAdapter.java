package com.pipiobjo.brewery.adapters.flametemp;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.vertx.core.eventbus.EventBus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Data
public class FlameTempMockAdapter implements FlameTempAdapter{

    @Inject
    private BreweryHardwareSimulation breweryHardwareSimulation;

    @Override
    public FlameTemperature getFlameTemp() {
        FlameTemperature result = new FlameTemperature();
        result.setTimestamp(OffsetDateTime.now());

        log.info(" -------- befor breweryHardware call methoed");
//        result.setTemperature(Optional.of(breweryHardwareSimulation.getFlameTemp()));
        return result;
    }

    @Override
    public void close() {
        log.info("close");
    }
}
