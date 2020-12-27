package com.pipiobjo.brewery.sensors;

import com.diozero.api.ThermometerInterface;
import com.diozero.devices.W1ThermSensor;
import com.diozero.util.RuntimeIOException;
import com.pipiobjo.brewery.adapters.FlameTempSensor;
import io.quarkus.runtime.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DS18B20 {
    private static final Logger log = LoggerFactory.getLogger(DS18B20.class);
    private final String devicePath;
    private W1ThermSensor device = null;

    public DS18B20(String devicePath){
        this.devicePath = devicePath;
        log.info("checking for sensors");
        List<W1ThermSensor> availableSensors = W1ThermSensor.getAvailableSensors();
        availableSensors.forEach(sensor -> {
            log.info("available sensorsType {} serialNo {} temperature{}", sensor.getType(), sensor.getSerialNumber(), sensor.getTemperature());
        });

        log.info("check for single device 28-012033890289");
        W1ThermSensor.getAvailableSensors("/sys/bus/w1/devices/w1_bus_master1/").forEach( sensor -> {
            log.info("available sensorsType {} serialNo {} temperature{}", sensor.getType(), sensor.getSerialNumber(), sensor.getTemperature());
        });



    }


public List<W1ThermSensor> getTempSensors(){

    if(devicePath == null || "".equals(devicePath)){
        return W1ThermSensor.getAvailableSensors();
    }else{
        return W1ThermSensor.getAvailableSensors("/sys/bus/w1/devices/w1_bus_master1/").stream()
                .filter(sensor -> sensor.getType().equals(W1ThermSensor.Type.DS18S20))
                .collect(Collectors.toList());
    }

}



}
