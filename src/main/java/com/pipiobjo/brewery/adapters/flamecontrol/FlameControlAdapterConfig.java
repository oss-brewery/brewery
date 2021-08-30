package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Slf4j
@Startup
@ApplicationScoped
public class FlameControlAdapterConfig {

    String activeProfile = ProfileManager.getActiveProfile();
    String launchMode = LaunchMode.current().name();
    FlameControlAdapter adapter = null;

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @Produces
    @IfBuildProfile("mockDevices")
    public FlameControlAdapter provideMock(){
        log.info("Selecting mocking device for flame control, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new FlameControlMockAdapter(breweryHardwareSimulation);
        return adapter;
    }


    @Produces
    @DefaultBean
    public FlameControlAdapter provideDevice() {
        log.info("Selecting real device for flame control, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new FlameControlDeviceAdapter();
        return adapter;
    }


}
