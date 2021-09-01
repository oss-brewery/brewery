package com.pipiobjo.brewery.services.controller;

import com.pipiobjo.brewery.adapters.flametemp.FlameTempAdapter;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.configuration.ProfileManager;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;

//@Data
@Slf4j
@Startup
@ApplicationScoped
public class GeneralControllerCycle {

    private static final long CALCULATION_TICK_TIME_MS = 100;

    // Testing Values for Simulation
    private BigDecimal setPointBurnerTemp = BigDecimal.valueOf(500);
    private int cycleCount = 0;
    private int cycleCountModulo = 320;

    @Inject
    BurnerController burnerController;
//    @Inject
//    BreweryHardwareSimulation breweryHardwareSimulation;
    @Inject
    FlameTempAdapter flameTempAdapter;

    private Cancellable cancellable;

    @PostConstruct
    public void init() {
        log.info("general controller is starting with tick time: {}ms", CALCULATION_TICK_TIME_MS);

        Multi<Long> ticks = Multi.createFrom().ticks().every(Duration.ofMillis(CALCULATION_TICK_TIME_MS));
        if(this.cancellable != null){
            log.info("general controller is already running");
        }

        this.cancellable = ticks.subscribe().with( it -> {
            this.calculate(BigDecimal.valueOf(CALCULATION_TICK_TIME_MS));
        });

    }

    private void calculate(BigDecimal stepSizeBD) {
        // calculation cycle for controller
        if (ProfileManager.getActiveProfile().equals("mockDevices")){

            burnerControllerFunction(stepSizeBD);

            // manual testing
            if (cycleCount % cycleCountModulo == 0) {
                if (setPointBurnerTemp.equals(BigDecimal.valueOf(500))){
                    setPointBurnerTemp = BigDecimal.valueOf(100);
                }else
                {
                    setPointBurnerTemp = BigDecimal.valueOf(500);
                }
            }
            cycleCount++;
        }
    }

    private void burnerControllerFunction(BigDecimal stepSizeBD){
         burnerController.calculate(stepSizeBD, setPointBurnerTemp, flameTempAdapter.getFlameTemp().getTemperature().get());
    }

    void onStop(@Observes ShutdownEvent ev) {
        if (cancellable != null) {
            cancellable.cancel();
            log.info("general controller stopped");
        } else {
            log.info("nothing to stop");
        }
    }
}
