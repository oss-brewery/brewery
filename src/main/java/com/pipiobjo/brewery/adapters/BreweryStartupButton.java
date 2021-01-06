package com.pipiobjo.brewery.adapters;

import com.diozero.api.DigitalInputEvent;
import com.diozero.api.GpioPullUpDown;
import com.diozero.devices.Button;
import io.quarkus.runtime.ShutdownEvent;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

//@ApplicationScoped
//public class BreweryStartupButton {
//    public final static String BREWERY_STARTUP_EVENT_NAME="BREWERY_STARTUP_EVENT_NAME";
//    private Logger log = LoggerFactory.getLogger(BreweryStartupButton.class);
//
//    @Inject
//    protected EventBus bus;
//
//    @Inject
//    protected DeviceManager deviceManager;
//
//    Button button = null;
//    private long lastExecutionTime =0;
//
//    @PostConstruct
//    void init() {
//        log.info("init button");
//        button = new Button(deviceManager.getBreweryStartButtonPort(), GpioPullUpDown.PULL_DOWN);
//        button.addListener( event -> {
//            fireEvent(event);
//        });
//
//    }
//
//
//    public void test(){
//        log.info("init from brewing service");
//    }
//    public void fireEvent(DigitalInputEvent event){
//        if(event.getGpio() == deviceManager.getBreweryStartButtonPort() && event.getValue()){
//            long lastExecutionDiff = TimeUnit.SECONDS.convert(event.getNanoTime() - lastExecutionTime, TimeUnit.NANOSECONDS);
//            if(lastExecutionDiff > 1 ){
//                this.lastExecutionTime = event.getNanoTime();
//                log.info("valueChanged({})", event);
//                bus.publish(BREWERY_STARTUP_EVENT_NAME, "someValues");
//            }
//
//        }
//
//    }
//
//    void onStop(@Observes ShutdownEvent ev) {
//        log.info("The application is stopping...");
//        button.close();
//    }
//}
