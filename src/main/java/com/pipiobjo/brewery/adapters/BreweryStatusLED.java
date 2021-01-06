package com.pipiobjo.brewery.adapters;

import com.diozero.devices.LED;
import com.diozero.util.SleepUtil;
import io.quarkus.runtime.ShutdownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;


//@ApplicationScoped
//public class BreweryStatusLED {
//    private static final Logger log = LoggerFactory.getLogger(BreweryStatusLED.class);
//
//    @Inject
//    protected DeviceManager deviceManager;
//
//    private LED led = null;
//
//    @PostConstruct
//    void init() {
//        if(led == null){
//            try{
//                this.led = new LED(deviceManager.getStatusLedPort());
//                log.info("led initialized");
//            }catch (Exception e){
//                log.error("Error while init led", e);
//            }
//        }
//    }
//
//    public void doMagic(){
//        led.on();
//        SleepUtil.sleepSeconds(.5);
//        led.off();
//        SleepUtil.sleepSeconds(.2);
//        led.toggle();
//        SleepUtil.sleepSeconds(.2);
//        led.toggle();
//        SleepUtil.sleepSeconds(.1);
//        led.blink(0.5f, 0.5f, 10, false);
//        led.off();
//    }
//
//    void onStop(@Observes ShutdownEvent ev) {
//        log.info("The application is stopping...");
//        led.off();
//        led.close();
//    }
//
//}
