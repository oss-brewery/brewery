package com.pipiobjo.brewery.adapters.inpot;

import io.quarkus.arc.DefaultBean;
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
public class InPotTemperatureAdapterConfig {

    @Inject
    InPotTemperatureConfigProperties config;
    String activeProfile = ProfileManager.getActiveProfile();
    String launchMode = LaunchMode.current().name();


    @Produces
    @DefaultBean
    public InPotTemperatureAdapter provideDevice() {
        log.info("Selecting real device for inpot temperatures, profile={}, launchMode={}", activeProfile, launchMode);
        return new InPotTemperatureDeviceAdapter(config);
    }



}
