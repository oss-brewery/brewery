package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.flametemp.FlameTempSensor;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureAdapter;
import com.pipiobjo.brewery.adapters.inpot.InpotTemperature;
import com.pipiobjo.brewery.services.model.SelfCheckResult;
import io.quarkus.scheduler.Scheduler;
import io.quarkus.vertx.ConsumeEvent;
import io.reactivex.disposables.CompositeDisposable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class SensorCollectorService {
    public static final String START_SELFCHECK = "START_SELFCHECK_SENSOR_DATA_COLLECTION";
    private final CompositeDisposable disposables = new CompositeDisposable();
    @Inject InPotTemperatureAdapter inPotTemperatureAdapter;
    @Inject FlameTempSensor flameTempSensor;
    @Inject Scheduler scheduler;
    @Inject SensorCollectorServiceConfigProperties config;
    private Cancellable cancellable;

    public SelfCheckResult executeSelfCheck() {

        log.info("Starting self check");
        SelfCheckResult result = new SelfCheckResult();
        inPotTemperatureAdapter.checkConfiguration();
        InpotTemperature inpotTemp = this.inPotTemperatureAdapter.getTemparatures();
        log.info("inpotTemp={}", inpotTemp);
        result.setInpotTemperature(inpotTemp);

        FlameTemperature flameTemp = flameTempSensor.getFlameTemp();
        log.info("flameTemp={}", flameTemp);
        result.setFlameTemp(flameTemp);

        return result;
    }


//    @ConsumeEvent(value = START_SELFCHECK, blocking = true)
//    public void executeSelfCheckEvent(String event) {
//
//    }

    public void stopCollecting() {
        cancellable.cancel();
    }

    public void startCollecting() throws InterruptedException {
        Multi<Long> ticks = Multi.createFrom().ticks().every(Duration.ofMillis(config.getBaseCollectionIntervallInMS()));

        this.cancellable = ticks.subscribe().with(
                it -> {
                    log.info("iteration {}", it);
                    List<CollectionPublishMode> mode = selectCollectionMode(it, config);
                    InpotTemperature temparatures = inPotTemperatureAdapter.getTemparatures();
                    FlameTemperature flameTemp = flameTempSensor.getFlameTemp();

                }

        );

    }

    private List<CollectionPublishMode> selectCollectionMode(Long it, SensorCollectorServiceConfigProperties config) {
        List<CollectionPublishMode> result = new ArrayList<>();

        // there should be no negative or 0 value
        if(it<=0L){
            return result;
        }

        long executionTime = it * config.getBaseCollectionIntervallInMS();

        if(executionTime < config.getInputCollectionIntervallInMS()){
            //return result;    // realy nessasary?
        }

        //        result.add(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS);
        // easy with n-times --> more like an init
        if (0==config.getInputCollectionIntervallInMS()%config.getBaseCollectionIntervallInMS()) {
            // n-times multiple of the Base Interval
            if (0==executionTime%config.getInputCollectionIntervallInMS()) {
                // exce
                result.add(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);
            }
        }else{
            // odd multiple of the Base Interval
            if (1>config.getInputCollectionIntervallInMS()/config.getBaseCollectionIntervallInMS()){
                // In is faster then the Base Interval
                // activate polling --> maybe a warning?
                result.add(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);
            }else if(0>=((config.getBaseCollectionIntervallInMS()+executionTime)%config.getInputCollectionIntervallInMS())-config.getBaseCollectionIntervallInMS()){
                // between to Base Interval --> choosing lower Base Interval
                result.add(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);
            }
        }


        return result;
    }


}

