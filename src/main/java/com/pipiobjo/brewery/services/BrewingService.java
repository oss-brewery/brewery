package com.pipiobjo.brewery.services;

import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.SelfCheckResult;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BrewingService{
    private Logger log = LoggerFactory.getLogger(BrewingService.class);

    @Inject
    EventBus bus;

    @Inject
    SensorCollectorService sensorCollectorService;
//    @Inject
//    protected BreweryStartupButton breweryStartupButton;

//    @Inject
//    protected FlameTempSensor flameTempSensor;

//    @Inject
//    protected BreweryStatusLED breweryStatusLED;

    public void init() {
        log.info("init brewing service");
//        breweryStartupButton.test();

    }

//    @ConsumeEvent(value = BreweryStartupButton.BREWERY_STARTUP_EVENT_NAME, blocking = true)
//    public void consume(String event) {
//        log.info("Start Button is pushed ... now lets rule the brewing process");
////        breweryStatusLED.doMagic();
//    }

    /**
     * Executing brewery self check. Includes checks for:
     * <ul>
     *     <li>Are the expected sensors reachable and do we receive data from them</li>
     *     <li>Can we persist sensor data to the configured location</li>
     *     <li>TODO: Can we check the security mechanism for gas flow, maybe turn gas off and let the gas firing system autodetect the failure? But this needs user interaction</li>
     * </ul>
     */
    public SelfCheckResult runSelfcheck() {
//        bus.publish(SensorCollectorService.START_SELFCHECK, "name");
        SelfCheckResult result = sensorCollectorService.executeSelfCheck();
        return result;
    }

    public void startCollecting(){
            sensorCollectorService.startCollecting();
    }

    public void stopCollecting(){
        sensorCollectorService.stopCollecting();
    }

}
