package com.pipiobjo.brewery.adapters.controlcabinet;

import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import com.pipiobjo.brewery.services.simulation.Pt1Sys;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
public class ControlCabinetMockAdapter implements ControlCabinetAdapter{

//    @Inject
//    BreweryHardwareSimulation breweryHardwareSimulation;

    public ControlCabinetMockAdapter(SensorCollectorServiceConfigProperties configMock) {

    }

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public ControlCabinetTemperature getTemperatures() {
        ControlCabinetTemperature result = new ControlCabinetTemperature();
        result.setTimestamp(OffsetDateTime.now());
//        result.setAirTemp(Optional.of(breweryHardwareSimulation.getAirTemp()));
//        result.setControlCabinetAirTemp(Optional.of(breweryHardwareSimulation.getControlCabinetAirTemp()));
        return result;
    }
}
