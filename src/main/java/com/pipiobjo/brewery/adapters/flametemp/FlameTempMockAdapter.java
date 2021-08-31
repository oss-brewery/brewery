package com.pipiobjo.brewery.adapters.flametemp;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class FlameTempMockAdapter implements FlameTempAdapter{

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @PostConstruct
    void init() {
        String activeProfile = ProfileManager.getActiveProfile();
        String launchMode = LaunchMode.current().name();
        log.info("Selecting mocking device for flame temperatures, profile={}, launchMode={}", activeProfile, launchMode);
    }

    @Override
    public FlameTemperature getFlameTemp() {
        FlameTemperature result = new FlameTemperature();
        result.setTimestamp(OffsetDateTime.now());

        result.setTemperature(Optional.of(breweryHardwareSimulation.getFlameTempSensor()));
        return result;
    }

    @Override
    public void close() {
        log.info("close");
    }
}
