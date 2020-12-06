package com.pipiobjo.brewery.adapters;

import com.diozero.api.SpiClockMode;
import com.pipiobjo.brewery.sensors.MAX6675V12;
import io.quarkus.runtime.ShutdownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * https://www.sparkfun.com/datasheets/IC/MAX6675.pdf
 */
@ApplicationScoped
public class FlameTempSensor {
    Logger log = LoggerFactory.getLogger(FlameTempSensor.class);


    //chiselect == cs // ss -> 0 / 1
    int chipselect = 0;
    int freq = 1000000;
    //modes cpha oder cpol
    boolean lsbFirst = false; // leastSignifactBit kommt am ende
    MAX6675V12 tempSensor = null;

    public void spi(){
        log.info("init temp bus listener");
//        tempSensor = new MAX6675V12(0,chipselect, freq, SpiClockMode.MODE_0);
        float temperature = tempSensor.getTemperature();
        log.info("temp {}", temperature);

        tempSensor.close();
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
        if(tempSensor !=null){
            tempSensor.close();
        }
    }

}
