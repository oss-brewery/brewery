package com.pipiobjo.brewery.rest;

import com.pipiobjo.brewery.adapters.SPIExtensionBoard;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class SampleRest {

//    @Inject
//    BreweryStatusLED breweryStatusLED;

//    @Inject
//    FlameTempSensor flameTempSensor;

    @Inject
    SPIExtensionBoard spiExtensionBoard;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {


//      new DS18B20(null);
//        flameTempSensor.spi();
//        breweryStatusLED.doMagic();


        spiExtensionBoard.spi();



        return "hello2";
    }
}