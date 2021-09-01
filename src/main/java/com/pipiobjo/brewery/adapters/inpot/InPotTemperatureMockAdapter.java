package com.pipiobjo.brewery.adapters.inpot;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class InPotTemperatureMockAdapter implements InPotTemperatureAdapter {

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation = null;

    @PostConstruct
    void init() {
        String activeProfile = ProfileManager.getActiveProfile();
        String launchMode = LaunchMode.current().name();
        log.info("Selecting mocking device for inpot temperatures, profile={}, launchMode={}", activeProfile, launchMode);
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
