package com.pipiobjo.brewery.adapters.controlcabinet;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ProfileManager;
import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class ControlCabinetMockAdapter implements ControlCabinetAdapter{

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @PostConstruct
    void init() {
        String activeProfile = ProfileManager.getActiveProfile();
        String launchMode = LaunchMode.current().name();
        log.info("Selecting mocking device for control cabinet temperatures, profile={}, launchMode={}", activeProfile, launchMode);
    }

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public ControlCabinetTemperature getTemperatures() {
        ControlCabinetTemperature result = new ControlCabinetTemperature();
        result.setTimestamp(OffsetDateTime.now());
//        result.setAirTemp(Optional.of(breweryHardwareSimulation.getAirTemp())); // TODO change it back if not needed
        result.setAirTemp(Optional.of(breweryHardwareSimulation.getDebugValueForFrontEnd()));
        result.setControlCabinetAirTemp(Optional.of(breweryHardwareSimulation.getControlCabinetAirTemp()));
        return result;
    }
}
