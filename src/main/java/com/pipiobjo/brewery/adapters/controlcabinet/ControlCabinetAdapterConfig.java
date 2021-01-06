package com.pipiobjo.brewery.adapters.controlcabinet;

import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureAdapter;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureConfigProperties;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureDeviceAdapter;
import com.pipiobjo.brewery.adapters.inpot.InputTemperatureMockAdapter;
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
public class ControlCabinetAdapterConfig {

    @Inject
    ControlCabinetConfigProperties config;
    String activeProfile = ProfileManager.getActiveProfile();
    String launchMode = LaunchMode.current().name();
    ControlCabinetAdapter adapter = null;

    @Produces
    @IfBuildProfile("mockDevices")
    public ControlCabinetAdapter provideMock(){
        log.info("Selecting mocking device for control cabinet temperatures, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new ControlCabinetMockAdapter();
        return adapter;
    }


    @Produces
    @DefaultBean
    public ControlCabinetAdapter provideDevice() {
        log.info("Selecting real device for control cabinet temperatures, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new ControlCabinetDeviceAdapter(config);
        return adapter;
    }

}
