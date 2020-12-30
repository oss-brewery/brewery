package com.pipiobjo.brewery.adapters.inpot;

import com.diozero.devices.W1ThermSensor;
import com.pipiobjo.brewery.sensors.DS18B20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

public class InPotTemperatureAdapter {

    private static final Logger log = LoggerFactory.getLogger(InPotTemperatureAdapter.class);
    private AtomicReference<W1ThermSensor> bottomSensor = null;
    private AtomicReference<W1ThermSensor> middleSensor = null;
    private AtomicReference<W1ThermSensor> topSensor = null;

    private DS18B20 device = null;

    @Inject
    private InPotTemperatureConfigProperties config;
    @PostConstruct
    void init() {
        if(device == null){
            log.info("init extension board device");
            device = new DS18B20(config.getW1TempDevicePath());
//            device = new DS18B20("/sys/bus/w1/devices/w1_bus_master1/");
        }
        checkConfiguration();

    }


    public void checkConfiguration() {


        device.getTempSensors().forEach(s -> {
            if (config.getW1TempBottomID().equals(s.getSerialNumber())) {
                bottomSensor.set(s);
            }
            if (config.getW1TempMiddleID().equals(s.getSerialNumber())) {
                middleSensor.set(s);
            }
            if (config.getW1TempTopID().equals(s.getSerialNumber())) {
                topSensor.set(s);
            }
        });

        if(bottomSensor == null){
            log.error("No w1 bottom sensor for ID {} found", config.getW1TempBottomID());
        }

        if(middleSensor == null){
            log.error("No w1 middle sensor for ID {} found", config.getW1TempMiddleID());
        }

        if(topSensor == null){
            log.error("No w1 middle sensor for ID {} found", config.getW1TempTopID());
        }

        if(bottomSensor == null || middleSensor == null | topSensor == null){
            log.error("Available w1 sensors are: ");
            device.getTempSensors().forEach(s -> {
                log.error("Sensor: type={}, serialNo={}, temp={}", s.getType(), s.getSerialNumber(), s.getTemperature());
            });
        }
    }


    public void getTemparatures(){
        bottomSensor.get().getTemperature();
    }
}
