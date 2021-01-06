package com.pipiobjo.brewery.adapters.controlcabinet;

import com.diozero.devices.W1ThermSensor;
import com.pipiobjo.brewery.sensors.DS18B20;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@DefaultBean
@ApplicationScoped
public class ControlCabinetDeviceAdapter implements ControlCabinetAdapter {

    private AtomicReference<W1ThermSensor> controlCabinetTempSensor = new AtomicReference<W1ThermSensor>();
    private AtomicReference<W1ThermSensor> airTempSensor = new AtomicReference<W1ThermSensor>();
    private DS18B20 device = null;

    private final ControlCabinetConfigProperties config;

    @PostConstruct
    void init() {
        if(device == null){
            device = new DS18B20(config.getW1TempDevicePath());
        }
    }


    @Override
    public void checkConfiguration() {


        device.getTempSensors().forEach(s -> {
            if (config.getW1AirTempInCabinetID().equals(s.getSerialNumber())) {
                controlCabinetTempSensor.set(s);
            }
            if (config.getW1AirTempID().equals(s.getSerialNumber())) {
                airTempSensor.set(s);
            }

        });

        if(controlCabinetTempSensor == null){
            log.error("No w1 bottom sensor for ID {} found", config.getW1AirTempInCabinetID());
        }

        if(airTempSensor == null){
            log.error("No w1 bottom sensor for ID {} found", config.getW1AirTempInCabinetID());
        }

        if(controlCabinetTempSensor == null | airTempSensor == null){
            log.error("No control cabinet air temp sensors is configured. Available w1 sensors are: ");
            device.getTempSensors().forEach(s -> {
                log.error("Sensor: type={}, serialNo={}, temp={}", s.getType(), s.getSerialNumber(), s.getTemperature());
            });
        }
    }


    @Override
    public ControlCabinetTemperature getTemparatures(){
        ControlCabinetTemperature result = new ControlCabinetTemperature();

        OffsetDateTime now = OffsetDateTime.now();
        result.setTimestamp(now);

        if(controlCabinetTempSensor != null && controlCabinetTempSensor.get() != null){
            result.setControlCabinetAirTemp(Optional.of(BigDecimal.valueOf(controlCabinetTempSensor.get().getTemperature())));
        }else {
            result.setControlCabinetAirTemp(Optional.empty());
        }

        if(airTempSensor != null && airTempSensor.get() != null){
            result.setAirTemp(Optional.of(BigDecimal.valueOf(airTempSensor.get().getTemperature())));
        }else {
            result.setControlCabinetAirTemp(Optional.empty());
        }

        return result;

    }
}


