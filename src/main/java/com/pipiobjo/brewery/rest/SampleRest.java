package com.pipiobjo.brewery.rest;

import com.pipiobjo.brewery.adapters.BreweryStatusLED;
import com.pipiobjo.brewery.adapters.FlameTempSensor;
import com.pipiobjo.brewery.adapters.SPIExtensionBoard;
import com.pipiobjo.brewery.sensors.DS18B20;

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

//        new ExtensionBoard();
//        new DS18B20();

//        spiExtensionBoard.spi();

//        flameTempSensor.spi();
//        breweryStatusLED.doMagic();


        spiExtensionBoard.spi();

//        SPIExtensionBoard board = new SPIExtensionBoard();
//        board.spi();


        return "hello2";
    }
}