package com.pipiobjo.brewery.adapters.flametemp;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
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
public class FlameTempAdapterConfig {
    @Inject
    FlameTempConfigProperties config;
    String activeProfile = ProfileManager.getActiveProfile();
    String launchMode = LaunchMode.current().name();
    FlameTempAdapter adapter;


    @Produces
    @IfBuildProfile("mockDevices")
    public FlameTempAdapter provideMock() {
        log.info("Selecting mocking device for flame temperatures, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new FlameTempMockAdapter();
        return adapter;
    }


    @Produces
    @DefaultBean
    public FlameTempAdapter provideDevice() {
        log.info("Selecting real device for flame temperatures, profile={}, launchMode={}", activeProfile, launchMode);
        adapter = new FlameTempDeviceAdapter(config);
        return adapter;
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
        if (adapter != null) {
            adapter.close();
        }
    }
}
