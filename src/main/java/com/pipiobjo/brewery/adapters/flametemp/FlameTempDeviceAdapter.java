package com.pipiobjo.brewery.adapters.flametemp;

import com.diozero.api.SpiClockMode;
import com.pipiobjo.brewery.sensors.MAX6675V12;
import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.ShutdownEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * https://www.sparkfun.com/datasheets/IC/MAX6675.pdf
 */
@Slf4j
@RequiredArgsConstructor
@DefaultBean
@ApplicationScoped
public class FlameTempDeviceAdapter implements FlameTempAdapter {
    private static final int controller = 0;
    //chiselect == cs // ss -> 0 / 1
    private static final int chipselect = 1;
    private static final int freq = 1000000;
    //modes cpha oder cpol
    private static final boolean lsbFirst = false; // leastSignifactBit kommt am ende

    private final FlameTempConfigProperties config;


    private MAX6675V12 tempSensor = null;

    @PostConstruct
    void init() {
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

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
        if (tempSensor != null) {
            tempSensor.close();
        }
    }

}
