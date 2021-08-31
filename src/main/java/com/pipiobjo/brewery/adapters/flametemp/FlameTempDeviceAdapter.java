package com.pipiobjo.brewery.adapters.flametemp;

import com.diozero.api.SpiClockMode;
import com.pipiobjo.brewery.sensors.MAX6675V12;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * https://www.sparkfun.com/datasheets/IC/MAX6675.pdf
 */
@Slf4j
public class FlameTempDeviceAdapter implements FlameTempAdapter {

    private MAX6675V12 tempSensor = null;
    private FlameTempConfigProperties config;

    public FlameTempDeviceAdapter(FlameTempConfigProperties config) {
        this.config = config;
        if (tempSensor == null) {
            tempSensor = new MAX6675V12(config.getController(), config.getChipselect(), config.getFreq(), SpiClockMode.MODE_0);
        }
    }


    @Override
    public FlameTemperature getFlameTemp() {
        FlameTemperature result = new FlameTemperature();
        result.setTimestamp(OffsetDateTime.now());
        Optional<BigDecimal> temperature = Optional.of(BigDecimal.valueOf(tempSensor.getTemperature()));
        result.setTemperature(temperature);
        return result;

    }

    @Override
    public void close() {
        if (tempSensor != null) {
            tempSensor.close();
        }
    }

}
