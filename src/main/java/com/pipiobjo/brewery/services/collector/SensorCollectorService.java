package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTempSensor;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureAdapter;
import com.pipiobjo.brewery.adapters.inpot.InpotTemperature;
import com.pipiobjo.brewery.services.model.SelfCheckResult;
import io.reactivex.disposables.CompositeDisposable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class SensorCollectorService {
    public static final String START_SELFCHECK = "START_SELFCHECK_SENSOR_DATA_COLLECTION";

    @Inject InPotTemperatureAdapter inPotTemperatureAdapter;
    @Inject FlameTempSensor flameTempSensor;
    @Inject ControlCabinetAdapter controlCabinetAdapter;
    @Inject SensorCollectorServiceConfigProperties config;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private Cancellable cancellable;

    public SelfCheckResult executeSelfCheck() {

        log.info("Starting self check");
        SelfCheckResult result = new SelfCheckResult();

        inPotTemperatureAdapter.checkConfiguration();
        InpotTemperature inpotTemp = this.inPotTemperatureAdapter.getTemparatures();
        log.info("inpotTemp={}", inpotTemp);
        result.setInpotTemperature(inpotTemp);

        controlCabinetAdapter.checkConfiguration();
        ControlCabinetTemperature controlCabinetTemp = controlCabinetAdapter.getTemparatures();
        log.info("controlCabinetTemp={}", controlCabinetTemp);
        result.setControlCabinetTemperature(controlCabinetTemp);

        FlameTemperature flameTemp = flameTempSensor.getFlameTemp();
        log.info("flameTemp={}", flameTemp);
        result.setFlameTemperature(flameTemp);

        return result;
    }


//    @ConsumeEvent(value = START_SELFCHECK, blocking = true)
//    public void executeSelfCheckEvent(String event) {
//
//    }

    public void stopCollecting() {
        if(cancellable != null){
            cancellable.cancel();
            log.info("collecting stopped");
        }else{
            log.info("nothing to stop");
        }
    }

    public void startCollecting() {
        Multi<Long> ticks = Multi.createFrom().ticks().every(Duration.ofMillis(config.getBaseCollectionIntervallInMS()));

        this.cancellable = ticks.subscribe().with(
                it -> {
                    log.debug("iteration {}", it);
                    StopWatch watch = new StopWatch();
                    List<CollectionPublishMode> mode = selectCollectionMode(it, config);

                    if(mode.contains(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR)){
                        watch.start();
                        FlameTemperature flameTemp = flameTempSensor.getFlameTemp();
                        watch.stop();
                        log.debug("flameTemp in {} ms: {}", watch.getTime(), flameTemp);
                        watch.reset();
                    }

                    if(mode.contains(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS)){
                        watch.start();
                        InpotTemperature inpotTemp = inPotTemperatureAdapter.getTemparatures();
                        watch.stop();
                        log.info("inpotTemp in {} ms: {}", watch.getTime(), inpotTemp);
                        watch.reset();


                        watch.start();
                        ControlCabinetTemperature controlCabinetTemp = controlCabinetAdapter.getTemparatures();
                        watch.stop();
                        log.info("controlCabinetTemp in {} ms: {}", watch.getTime(), controlCabinetTemp);
                        watch.reset();

                    }

                    if(mode.contains(CollectionPublishMode.PUBLISH_TO_CALCULATION)){
                        log.debug("publish to calc");
                    }


                    if(mode.contains(CollectionPublishMode.PUBLISH_TO_UI)){
                        log.info("publish to ui");
                    }

                    if(mode.contains(CollectionPublishMode.PUBLISH_TO_PERSISTENCE)){
                        log.info("publish to persistence");
                    }


                }

        );

    }

    private List<CollectionPublishMode> selectCollectionMode(Long it, SensorCollectorServiceConfigProperties config) {
        List<CollectionPublishMode> result = new ArrayList<>();

        // there should be no negative or 0 value
        if (it <= 0L) {
            return result;
        }

        long executionTime = it * config.getBaseCollectionIntervallInMS();

        if(isModeAddable(config.getBaseCollectionIntervallInMS(), config.getInputCollectionIntervallInMS(), executionTime)){
            result.add(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);
        }

        if(isModeAddable(config.getBaseCollectionIntervallInMS(), config.getTemperatureCollectionIntervallInMS(), executionTime)){
            result.add(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS);
        }

        if(isModeAddable(config.getBaseCollectionIntervallInMS(), config.getPersistenceIntervallInMS(), executionTime)){
            result.add(CollectionPublishMode.PUBLISH_TO_PERSISTENCE);
        }

        if(isModeAddable(config.getBaseCollectionIntervallInMS(), config.getUiUpdateIntervallInMS(), executionTime)){
            result.add(CollectionPublishMode.PUBLISH_TO_UI);
        }

        if(isModeAddable(config.getBaseCollectionIntervallInMS(), config.getCalculationIntervallInMS(), executionTime)){
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


}

