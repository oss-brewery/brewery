package com.pipiobjo.brewery.adapters.controlcabinet;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.profile.IfBuildProfile;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@IfBuildProfile("mockDevices")
@ApplicationScoped
public class ControlCabinetMockAdapter implements ControlCabinetAdapter{

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;
//    BreweryHardwareSimulation breweryHardwareSimulation = null;

//    public ControlCabinetMockAdapter(BreweryHardwareSimulation breweryHardwareSimulation){
//        this.breweryHardwareSimulation = breweryHardwareSimulation;
//    }

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public ControlCabinetTemperature getTemperatures() {
        ControlCabinetTemperature result = new ControlCabinetTemperature();
        result.setTimestamp(OffsetDateTime.now());
        result.setAirTemp(Optional.of(breweryHardwareSimulation.getAirTemp()));
        result.setControlCabinetAirTemp(Optional.of(breweryHardwareSimulation.getControlCabinetAirTemp()));
        return result;
    }
}
