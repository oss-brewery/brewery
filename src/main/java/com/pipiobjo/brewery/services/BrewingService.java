package com.pipiobjo.brewery.services;

import com.pipiobjo.brewery.adapters.BreweryStartupButton;
import com.pipiobjo.brewery.adapters.BreweryStatusLED;
import com.pipiobjo.brewery.adapters.FlameTempSensor;
import com.pipiobjo.brewery.adapters.SPIExtensionBoard;
import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BrewingService{
    private Logger log = LoggerFactory.getLogger(BrewingService.class);

    @Inject
    protected BreweryStartupButton breweryStartupButton;

    @Inject
    protected FlameTempSensor flameTempSensor;

    @Inject
    protected BreweryStatusLED breweryStatusLED;

    public void init() {
        log.info("init brewing service");
        breweryStartupButton.test();

    }

    @ConsumeEvent(value = BreweryStartupButton.BREWERY_STARTUP_EVENT_NAME, blocking = true)
    public void consume(String event) {
        log.info("Start Button is pushed ... now lets rule the brewing process");
        breweryStatusLED.doMagic();
    }

}
