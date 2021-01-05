package com.pipiobjo.brewery.services.collector;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PiCalculatorTest {



    @Test
    public void calculatePiAdjustment() throws Exception {
        PiCalculator calculator = new PiCalculator();
        Long stepSize = 10L;
        Long it = 1L;

        StopWatch stopWatch = new StopWatch();
        SensorCollectorServiceConfigProperties config = new SensorCollectorServiceConfigProperties();
        config.setBaseCollectionIntervallInMS(stepSize);


        BigDecimal targetTemp = BigDecimal.valueOf(1000);
        BigDecimal flameTemp = BigDecimal.valueOf(0);

        for (it = 1L; it < 10L; it++) {

            BigDecimal adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp);
            assertThat(adjustment).isEqualTo(BigDecimal.valueOf(stepSize*it));
        }

    }

}