package com.pipiobjo.brewery.rest;

import com.pipiobjo.brewery.adapters.BreweryStatusLED;
import com.pipiobjo.brewery.adapters.ExtensionBoard;
import com.pipiobjo.brewery.adapters.FlameTempSensor;
import com.pipiobjo.brewery.sensors.DS18B20;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class SampleRest {

    @Inject
    BreweryStatusLED breweryStatusLED;

    @Inject
    FlameTempSensor flameTempSensor;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        new ExtensionBoard();
        new DS18B20();
//        flameTempSensor.spi();
//        breweryStatusLED.doMagic();


        return "hello2";
    }
}