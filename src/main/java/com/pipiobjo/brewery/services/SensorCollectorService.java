package com.pipiobjo.brewery.services;

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

@Slf4j
@ApplicationScoped
public class SensorCollectorService {
    public static final String START_SELFCHECK = "START_SELFCHECK_SENSOR_DATA_COLLECTION";
    private final CompositeDisposable disposables = new CompositeDisposable();
    @Inject
    InPotTemperatureAdapter inpotTemp;

    @Inject
    FlameTempSensor flameTempSensor;

    @Inject
    Scheduler scheduler;
    private Cancellable cancellable;

    public SelfCheckResult executeSelfCheck() {

        log.info("Starting self check");
        SelfCheckResult result = new SelfCheckResult();
        inpotTemp.checkConfiguration();
        InpotTemperature inpotTemp = this.inpotTemp.getTemparatures();
        log.info("inpotTemp={}", inpotTemp);
        result.setInpotTemperature(inpotTemp);

        FlameTemperature flameTemp = flameTempSensor.getFlameTemp();
        log.info("flameTemp={}", flameTemp);
        result.setFlameTemp(flameTemp);

        return result;
    }


    @ConsumeEvent(value = START_SELFCHECK, blocking = true)
    public void executeSelfCheckEvent(String event) {

    }


    public void startCollecting() throws InterruptedException {
        Multi<Long> ticks = Multi.createFrom().ticks().every(Duration.ofMillis(2000));

        this.cancellable = ticks.subscribe().with(
                item -> {
                    log.info("value {}", item);
                    InpotTemperature temparatures = inpotTemp.getTemparatures();
                    FlameTemperature flameTemp = flameTempSensor.getFlameTemp();

                }

        );

    }


    public void stopCollecting() {
        cancellable.cancel();
    }

}

