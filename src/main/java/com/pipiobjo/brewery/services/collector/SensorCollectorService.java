package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTempAdapter;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureAdapter;
import com.pipiobjo.brewery.adapters.inpot.InpotTemperature;
import com.pipiobjo.brewery.services.model.CollectionResult;
import com.pipiobjo.brewery.services.model.SelfCheckResult;
import com.pipiobjo.brewery.services.simulation.SimulationAdapter;
import io.quarkus.runtime.ShutdownEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class SensorCollectorService {
    public static final String PUBLISH_TO_UI_EVENT_NAME = "BREWERY_PUBLISH_TO_UI_EVENT_NAME";
    public static final String PUBLISH_TO_CALCULATION_EVENT_NAME = "BREWERY_PUBLISH_TO_CALCULATION_EVENT_NAME";
    public static final String PUBLISH_TO_PERSISTENCE_EVENT_NAME = "BREWERY_PUBLISH_TO_PERSISTENCE_EVENT_NAME";
    public static final String START_SELFCHECK = "START_SELFCHECK_SENSOR_DATA_COLLECTION";

    @Inject
    InPotTemperatureAdapter inPotTemperatureAdapter;
    @Inject
    FlameTempAdapter flameTempAdapter;
    @Inject
    ControlCabinetAdapter controlCabinetAdapter;
    @Inject
    SPIExtensionBoardAdapter extensionBoard;
    @Inject
    EventBus bus;
    @Inject
    SensorCollectorServiceConfigProperties config;
    @Inject
    SimulationAdapter simulationAdapter;


    @Inject
    PiCalculator piCalculator;

    private Cancellable cancellable;

    public SelfCheckResult executeSelfCheck() {

        // TODO add flame control cycle check
        log.info("Starting self check");
        SelfCheckResult result = new SelfCheckResult();

        inPotTemperatureAdapter.checkConfiguration();
        InpotTemperature inpotTemp = this.inPotTemperatureAdapter.getTemperatures();
        log.info("inpotTemp={}", inpotTemp);
        result.setInpotTemperature(inpotTemp);

        controlCabinetAdapter.checkConfiguration();
        ControlCabinetTemperature controlCabinetTemp = controlCabinetAdapter.getTemparatures();
        log.info("controlCabinetTemp={}", controlCabinetTemp);
        result.setControlCabinetTemperature(controlCabinetTemp);

        FlameTemperature flameTemp = flameTempAdapter.getFlameTemp();
        log.info("flameTemp={}", flameTemp);
        result.setFlameTemperature(flameTemp);

        return result;
    }


    public void stopCollecting() {
        if (cancellable != null) {
            cancellable.cancel();
            log.info("collecting stopped");
        } else {
            log.info("nothing to stop");
        }
    }

    public void startCollecting() {
        Multi<Long> ticks = Multi.createFrom().ticks().every(Duration.ofMillis(config.getBaseCollectionIntervallInMS()));
        long targetTemp = 1000;                             // set point
        if(this.cancellable != null){
            log.info("collecting data is already running");
        }
        BigDecimal ki= BigDecimal.valueOf(1);               // I-gain
        BigDecimal kp= BigDecimal.valueOf(1);               // P-gain
        BigDecimal minPercent = BigDecimal.valueOf(0);
        BigDecimal maxPercent = BigDecimal.valueOf(100);

        this.cancellable = ticks.subscribe().with(
                it -> {
                    log.debug("iteration {}", it);
                    StopWatch watch = new StopWatch();
                    List<CollectionPublishMode> mode = selectCollectionMode(it, config);


                    CollectionResult result = new CollectionResult();

                    if (mode.contains(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR)) {
                        watch.start();
                        boolean flameControlButtonPushed = extensionBoard.isFlameControlButtonPushed();
                        watch.stop();
                        log.debug("flameControlButtonCheck in {} ms: {}", watch.getTime(), flameControlButtonPushed);
                        watch.reset();
                        result.setFlameControlButtonPushed(flameControlButtonPushed);
                    }

                    if (mode.contains(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS)) {

                        watch.start();
                        FlameTemperature flameTemp = flameTempAdapter.getFlameTemp();
                        watch.stop();
                        log.debug("flameTemp in {} ms: {}", watch.getTime(), flameTemp);
                        watch.reset();
                        result.setFlameTemperature(flameTemp);

                        picalculation(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, kp, ki, maxPercent, minPercent);

                        watch.start();
                        InpotTemperature inpotTemp = inPotTemperatureAdapter.getTemperatures();
                        watch.stop();
                        log.debug("inpotTemp in {} ms: {}", watch.getTime(), inpotTemp);
                        watch.reset();
                        result.setInpotTemperature(inpotTemp);


                        watch.start();
                        ControlCabinetTemperature controlCabinetTemp = controlCabinetAdapter.getTemparatures();
                        watch.stop();
                        log.debug("controlCabinetTemp in {} ms: {}", watch.getTime(), controlCabinetTemp);
                        watch.reset();
                        result.setControlCabinetTemperature(controlCabinetTemp);

                    }

                    if (mode.contains(CollectionPublishMode.PUBLISH_TO_CALCULATION)) {
                        log.debug("publish to calc");
                        simulationAdapter.calculate(BigDecimal.valueOf(config.getCalculationIntervallInMS()));
                        bus.publish(PUBLISH_TO_CALCULATION_EVENT_NAME, result);
                    }


                    if (mode.contains(CollectionPublishMode.PUBLISH_TO_UI)) {
                        log.debug("publish to ui");
                        bus.publish(PUBLISH_TO_UI_EVENT_NAME, result);
                    }

                    if (mode.contains(CollectionPublishMode.PUBLISH_TO_PERSISTENCE)) {
                        log.debug("publish to persistence");
                        bus.publish(PUBLISH_TO_PERSISTENCE_EVENT_NAME, result);
                    }
                }
        );
    }

    private BigDecimal picalculation(Long stepSize, long targetTemp, FlameTemperature flameTemp,BigDecimal kp,BigDecimal ki,BigDecimal maxPercent,BigDecimal minPercent) {
        return piCalculator.calculate(stepSize, BigDecimal.valueOf(targetTemp), kp, ki, flameTemp.getTemperature().get(), maxPercent,minPercent);
    }

    private List<CollectionPublishMode> selectCollectionMode(Long it, SensorCollectorServiceConfigProperties config) {
        List<CollectionPublishMode> result = new ArrayList<>();

        // there should be no negative or 0 value
        if (it <= 0L) {
            return result;
        }

        long executionTime = it * config.getBaseCollectionIntervallInMS();

        if (isModeAddable(config.getBaseCollectionIntervallInMS(), config.getInputCollectionIntervallInMS(), executionTime)) {
            result.add(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);
        }

        if (isModeAddable(config.getBaseCollectionIntervallInMS(), config.getTemperatureCollectionIntervallInMS(), executionTime)) {
            result.add(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS);
        }

        if (isModeAddable(config.getBaseCollectionIntervallInMS(), config.getPersistenceIntervallInMS(), executionTime)) {
            result.add(CollectionPublishMode.PUBLISH_TO_PERSISTENCE);
        }

        if (isModeAddable(config.getBaseCollectionIntervallInMS(), config.getUiUpdateIntervallInMS(), executionTime)) {
            result.add(CollectionPublishMode.PUBLISH_TO_UI);
        }

        if (isModeAddable(config.getBaseCollectionIntervallInMS(), config.getCalculationIntervallInMS(), executionTime)) {
            result.add(CollectionPublishMode.PUBLISH_TO_CALCULATION);
        }

        return result;
    }

    private boolean isModeAddable(long baseIntervall, long intervall2Check, long executionTime) {
        // easy with n-times --> more like an init
        if (0 == intervall2Check % baseIntervall) {
            // n-times multiple of the Base Interval
            if (0 == executionTime % intervall2Check) {
                // exec
                return true;
            }
        } else {
            // odd multiple of the Base Interval
            if (1 > intervall2Check / baseIntervall) {
                // In is faster then the Base Interval
                // activate polling --> maybe a warning?
                return true;
            }
            if (0 >= ((baseIntervall + executionTime) % intervall2Check) - baseIntervall) {
                // between to Base Interval --> choosing lower Base Interval
                return true;
            }
        }

        return false;
    }
    void onStop(@Observes ShutdownEvent ev) {
        stopCollecting();
    }

}

