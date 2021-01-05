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
        PiCalculator calculator2 = new PiCalculator();

        Long stepSize = 10L;
        Long it = 1L;

        StopWatch stopWatch = new StopWatch();
        SensorCollectorServiceConfigProperties config = new SensorCollectorServiceConfigProperties();
        config.setBaseCollectionIntervallInMS(stepSize);


        BigDecimal targetTemp = BigDecimal.valueOf(1000);
        BigDecimal flameTemp = BigDecimal.valueOf(990);
        BigDecimal maxPercent= BigDecimal.valueOf(100.0);     // saturation limit up
        BigDecimal minPercent= BigDecimal.valueOf(0.0);       // saturation limit low
        BigDecimal KP= BigDecimal.valueOf(1);               // P-gain
        BigDecimal KI= BigDecimal.valueOf(1);               // I-gain
        BigDecimal adjustment= BigDecimal.valueOf(0);

        // Test for numerical integration
        for (it = 1L; it < 10L; it++) {
            adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
            assertThat(adjustment).isEqualTo(BigDecimal.valueOf(stepSize*it).multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(1000)).add(BigDecimal.valueOf(10)));
        }

        // Test saturation upper
        for (it = 10L; it < 901L; it++) {
            assertThat(adjustment).isNotEqualTo(maxPercent);
            adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
        }
        assertThat(adjustment).isEqualTo(maxPercent);

        // Test for numerical integration --> second Instance
        for (it = 1L; it < 10L; it++) {
            adjustment = calculator2.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
            assertThat(adjustment).isEqualTo(BigDecimal.valueOf(stepSize*it).multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(1000)).add(BigDecimal.valueOf(10)));
        }

        // Test Anti-Wind-up upper limit
        for (it = 1L; it < 11L; it++) {
            adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
        }
        targetTemp = BigDecimal.valueOf(1000);
        flameTemp = BigDecimal.valueOf(1000);
        adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
        assertThat(adjustment).isEqualTo(BigDecimal.valueOf(90.0));

        // Test saturation lower
        targetTemp = BigDecimal.valueOf(990);
        flameTemp = BigDecimal.valueOf(1000);
        for (it = 1L; it < 801L; it++) {
            assertThat(adjustment).isNotEqualTo(minPercent);
            adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
        }
        assertThat(adjustment).isEqualTo(minPercent);

        // Test Anti-Wind-up lower limit
        for (it = 1L; it < 11L; it++) {
            adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
        }
        targetTemp = BigDecimal.valueOf(1000);
        flameTemp = BigDecimal.valueOf(1000);
        adjustment = calculator.calculate(config.getBaseCollectionIntervallInMS(), targetTemp, flameTemp, KP, KI, maxPercent, minPercent);
        assertThat(adjustment).isEqualTo(BigDecimal.valueOf(10.0));
    }

}