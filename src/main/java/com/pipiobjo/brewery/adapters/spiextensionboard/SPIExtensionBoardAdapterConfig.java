package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Slf4j
@Startup
@ApplicationScoped
public class SPIExtensionBoardAdapterConfig {

    @Inject
    SPIExtensionBoardAdapterConfigProperties config;
    String activeProfile = ProfileManager.getActiveProfile();
    String launchMode = LaunchMode.current().name();
    SPIExtensionBoardAdapter adapter;

    @Produces
    @DefaultBean
    public SPIExtensionBoardAdapter provideDevice() {
        log.info("Selecting real device for spiextensionboard, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new SPIExtensionBoardDeviceAdapter(config);
        return adapter;
    }

    void onStop(@Observes ShutdownEvent ev) {
        if(adapter!=null){
            log.info("Shutting down {}", ev);
            adapter.close();
        }
    }
}
