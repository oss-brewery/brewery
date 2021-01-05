package com.pipiobjo.brewery.services.simulation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class pt1SysTest {

    @Test
    void calculation() {
        BigDecimal timeConstant = BigDecimal.valueOf(1);
        BigDecimal kGain = BigDecimal.valueOf(1);
        BigDecimal stepSizeBD = BigDecimal.valueOf(10);
        BigDecimal initialCondition = BigDecimal.valueOf(0);
        pt1Sys pt1Sys = new pt1Sys(timeConstant, kGain, initialCondition);

        BigDecimal inputValue = BigDecimal.valueOf(1);
        BigDecimal output =BigDecimal.ZERO;

        for (long it = 1L; it < 101L; it++) {
            output = pt1Sys.calculate(stepSizeBD, inputValue);
        }
        assertThat(output).isGreaterThan(BigDecimal.valueOf(0.63));
        assertThat(output).isLessThan(BigDecimal.valueOf(0.64));
        for (long it = 1L; it < 11L; it++) {
            output = pt1Sys.calculate(stepSizeBD, inputValue);
        }
        assertThat(output).isGreaterThan(BigDecimal.valueOf(0.64));
    }
}