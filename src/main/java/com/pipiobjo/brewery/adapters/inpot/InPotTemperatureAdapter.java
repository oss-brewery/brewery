package com.pipiobjo.brewery.adapters.inpot;

import com.diozero.devices.W1ThermSensor;
import com.pipiobjo.brewery.sensors.DS18B20;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ApplicationScoped
public class InPotTemperatureAdapter {

    private AtomicReference<W1ThermSensor> bottomSensor = new AtomicReference<W1ThermSensor>();
    private AtomicReference<W1ThermSensor> middleSensor = new AtomicReference<W1ThermSensor>();
    private AtomicReference<W1ThermSensor> topSensor = new AtomicReference<W1ThermSensor>();

    private DS18B20 device = null;

    @Inject
    InPotTemperatureConfigProperties config;

    @PostConstruct
    void init() {
        if(device == null){
            device = new DS18B20(config.getW1TempDevicePath());
        }
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
            log.error("No inpot temp sensor is configured successfull. Available w1 sensors are: ");
            device.getTempSensors().forEach(s -> {
                log.error("Sensor: type={}, serialNo={}, temp={}", s.getType(), s.getSerialNumber(), s.getTemperature());
            });
        }
    }


    public InpotTemperature getTemparatures(){
        InpotTemperature result = new InpotTemperature();

        OffsetDateTime now = OffsetDateTime.now();
        result.setTimestamp(now);

        if(bottomSensor != null && bottomSensor.get() != null){
            result.setBottom(Optional.of(BigDecimal.valueOf(bottomSensor.get().getTemperature())));
        }else{
            result.setBottom(Optional.empty());
        }
        if(middleSensor != null && middleSensor.get() != null){
            result.setMiddle(Optional.of(BigDecimal.valueOf(middleSensor.get().getTemperature())));
        }else{
            result.setMiddle(Optional.empty());
        }
        if(topSensor != null && topSensor.get() != null){
            result.setTop(Optional.of(BigDecimal.valueOf(topSensor.get().getTemperature())));
        }else{
            result.setTop(Optional.empty());
        }
        return result;

    }
}
