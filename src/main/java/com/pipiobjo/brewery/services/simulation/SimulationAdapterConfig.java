package com.pipiobjo.brewery.services.simulation;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetConfigProperties;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetDeviceAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetMockAdapter;
import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
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
public class SimulationAdapterConfig {

    String activeProfile = ProfileManager.getActiveProfile();
    String launchMode = LaunchMode.current().name();
    SimulationAdapter adapter = null;

    @Produces
    @IfBuildProfile("mockDevices")
    public SimulationAdapter provideMock(){
        log.info("Selecting mocking device for simulating the hardware, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new SimulationMockAdapter();
        return adapter;
    }


    @Produces
    @DefaultBean
    public SimulationAdapter provideDevice() {
        log.info("Selecting real device for simulating the hardware, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new SimulationDeviceAdapter();
        return adapter;
    }

}
