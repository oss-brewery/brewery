package com.pipiobjo.brewery.sensors;

import com.diozero.devices.W1ThermSensor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DS18B20 {
    private final String devicePath;
    private W1ThermSensor device = null;

    /**
     *
     * @param devicePath - for example "/sys/bus/w1/devices/w1_bus_master1/"
     */
    public DS18B20(String devicePath) {
        this.devicePath = devicePath;

    }


    public List<W1ThermSensor> getTempSensors() {

        if (devicePath == null || "".equals(devicePath)) {
            return W1ThermSensor.getAvailableSensors();
        } else {

            return W1ThermSensor.getAvailableSensors(devicePath).stream()
                    .filter(sensor -> sensor.getType().name().equals(W1ThermSensor.Type.DS18B20.name()))
                    .collect(Collectors.toList());
        }

    }


}
