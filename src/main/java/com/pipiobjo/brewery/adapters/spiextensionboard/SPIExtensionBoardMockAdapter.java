package com.pipiobjo.brewery.adapters.spiextensionboard;

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
public class SPIExtensionBoardMockAdapter implements SPIExtensionBoardAdapter {

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @PostConstruct
    void init() {
        String activeProfile = ProfileManager.getActiveProfile();
        String launchMode = LaunchMode.current().name();
        log.info("Selecting mocking device for spi extension board, profile={}, launchMode={}", activeProfile, launchMode);
    }

    @Override
    public boolean isFlameControlButtonPushed() {
        return false;
    }

    @Override
    public void beepOn() {

    }

    @Override
    public void beepOff() {

    }

    @Override
    public void beepForMilliSeconds(long milliseconds) {

    }

    @Override
    public void motorControl(long deltaPosition) {

    }

    @Override
    public void turn230VRelaisOn() {

    }

    @Override
    public void turn230VRelaisOff() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isFlameOn() {
        return breweryHardwareSimulation.isFlameIsOn();
    }

}
