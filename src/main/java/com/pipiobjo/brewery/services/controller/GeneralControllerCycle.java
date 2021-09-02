package com.pipiobjo.brewery.services.controller;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetMockAdapter;
import com.pipiobjo.brewery.adapters.flametemp.FlameTempAdapter;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureAdapter;
import com.pipiobjo.brewery.adapters.monotorcontrol.MotorControlAdapter;
import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.CollectionResult;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.configuration.ProfileManager;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

//@Data
@Slf4j
@Startup
@ApplicationScoped
public class GeneralControllerCycle {

    private static final long CALCULATION_TICK_TIME_MS = 100;

    // Testing Values for Simulation
    private BigDecimal setPointInPotTemp = BigDecimal.valueOf(40);
    private BigDecimal setPointBurnerTemp = BigDecimal.valueOf(500);
    private int cycleCount = 0;
    private int cycleCountModulo = 500;

    @Inject
    BurnerController burnerController;
    @Inject
    InPotTempController inPotTempController;
    @Inject
    InPotTemperatureAdapter inPotTemperatureAdapter;
    @Inject
    FlameTempAdapter flameTempAdapter;
    @Inject
    MotorControlAdapter motorControlAdapter;
    @Inject
    ControlCabinetAdapter controlCabinetAdapter;
//    @Inject
//    BreweryHardwareSimulation breweryHardwareSimulation;

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

    @ConsumeEvent(value = SensorCollectorService.PUBLISH_TO_CALCULATION_EVENT_NAME, blocking = true) // TODO Asyncron = false
    public void reciveData(CollectionResult event){

    }

    private void calculate(BigDecimal stepSizeBD) {
        // calculation cycle for controller
        if (ProfileManager.getActiveProfile().equals("mockDevices")){

            inPotControllerFunction(stepSizeBD, setPointInPotTemp, inPotTemperatureAdapter.getTemperatures().getMiddle().get());
            BigDecimal feedForwardAdd = feedForward(controlCabinetAdapter.getTemperatures().getAirTemp().get());
            burnerControllerFunction(stepSizeBD, setPointBurnerTemp, flameTempAdapter.getFlameTemp().getTemperature().get(), feedForwardAdd);

            // manual testing
            if (cycleCount % cycleCountModulo == 0) {
                if (setPointInPotTemp.equals(BigDecimal.valueOf(40))){
                    setPointInPotTemp = BigDecimal.valueOf(35);
                }else
                {
                    setPointInPotTemp = BigDecimal.valueOf(40);
                }
            }
            cycleCount++;
//            setPointInPotTemp = BigDecimal.valueOf(19.5);  // TODO prof differential equation
        }
    }

    private void inPotControllerFunction(BigDecimal stepSizeBD, BigDecimal setPoint, BigDecimal feedback){
        inPotTempController.calculate(stepSizeBD, setPoint, feedback); // TODO find a way to use all temps
        setPointBurnerTemp = inPotTempController.getControllerOutputCelsius();
    }

    private void burnerControllerFunction(BigDecimal stepSizeBD, BigDecimal setPoint, BigDecimal feedback, BigDecimal feedForwardInput){
//         log.info("---------- feedforward input : {}", feedForwardInput);
         burnerController.calculate(stepSizeBD, setPoint, feedback, feedForwardInput);
         motorControlAdapter.moveToPercent(burnerController.getBurnerControllerOutputPercent());
         // TODO output is different to "inPotControllerFunction" --> redesign motorControlAdapter in the future in alignment to the other controllers
    }

    private BigDecimal feedForward(BigDecimal disturbanceCelsius){
        BigDecimal THERMAL_RESISTOR_AIR_2_BREW = BigDecimal.valueOf(0.0005);
        BigDecimal THERMAL_RESISTOR_BURNER_2_BREW = BigDecimal.valueOf(0.0035);
        return disturbanceCelsius.multiply(BigDecimal.valueOf(-1)).multiply(THERMAL_RESISTOR_BURNER_2_BREW).divide(THERMAL_RESISTOR_AIR_2_BREW, 15, RoundingMode.HALF_DOWN);
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
