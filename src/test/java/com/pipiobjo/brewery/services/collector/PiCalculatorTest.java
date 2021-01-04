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


        StopWatch stopWatch = new StopWatch();

        Long it = 1L;
        BigDecimal targetTemp = BigDecimal.valueOf(1000);
        BigDecimal flameTemp = BigDecimal.valueOf(50);

        long adjustment = calculator.calculate(it, targetTemp, flameTemp);

        assertThat(adjustment).isEqualTo(0L);
    }

}