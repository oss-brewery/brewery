package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class FlameControlMockAdapter implements FlameControlAdapter{

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @PostConstruct
    void init() {
        String activeProfile = ProfileManager.getActiveProfile();
        String launchMode = LaunchMode.current().name();
        log.info("Selecting mocking device for flame control, profile={}, launchMode={}", activeProfile, launchMode);
    }

    @Override
    public void turnOff() {

        breweryHardwareSimulation.setFlameIsOn(false);
    }

    @Override
    public void turnOn() {

        breweryHardwareSimulation.setFlameIsOn(true);
    }

    @Override
    public void increaseFlameByOneStep() {
        // TODO implement HardwareSimulation
    }

    @Override
    public void decreaseFlameByOneStep() {
        // TODO implement HardwareSimulation
    }

    @Override
    public boolean isFlameOn() {
        return breweryHardwareSimulation.isFlameIsOn();
    }
}
