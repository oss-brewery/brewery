package com.pipiobjo.brewery.interpolable;

import com.pipiobjo.brewery.interpolable.InterpolatingDouble;
import com.pipiobjo.brewery.services.collector.NormLinearization;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class NormLinearizationTest {

    @Test
    void calculation() {
        int N = 101;
        double start = 0;
        double end = 10;

        // creating "measured" points
        double[] measureingPoint = new double[N];
        for (int i = (int) start; i < N; i++) {
            measureingPoint[i] =  (double)(i * (end - start) / (N - 1));
        }
        // creating "measured" values
        double[] measureingValue = new double[N];
        for (int i = (int) start; i < N; i++) {
            measureingValue[i] =  Math.pow(measureingPoint[i],2);
        }
        NormLinearization nL1 = new NormLinearization(measureingPoint, measureingValue);

        log.info("normalisation = {}", nL1);

        InterpolatingDouble a = nL1.getMap().getInterpolated(new InterpolatingDouble(0.25));
        assertThat(a.getValue()).isEqualTo(0.5);

        InterpolatingDouble b = nL1.getMap().getInterpolated(new InterpolatingDouble(0.36));
        assertThat(b.getValue()).isEqualTo(0.6);

        InterpolatingDouble c = nL1.getMap().getInterpolated(new InterpolatingDouble(0.3));
        assertThat(c).isNotNull();
        assertThat(c.getValue()).isCloseTo(0.5454545454545454, Percentage.withPercentage(0.000001));

        BigDecimal tempnL1 = nL1.calculation(BigDecimal.valueOf(0.5),BigDecimal.valueOf(1));
        assertThat(tempnL1.doubleValue()).isCloseTo(0.706666,Percentage.withPercentage(0.001));

        // creating "measured" points
        for (int i = (int) start; i < N; i++) {
            measureingPoint[i] =  (double)(i * (end - start) / (N - 1));
        }
        // creating "measured" values
        for (int i = (int) start; i < N; i++) {
            measureingValue[i] =  Math.pow(measureingPoint[i],3);
        }
        NormLinearization sysDynamic = new NormLinearization(measureingPoint, measureingValue);
        NormLinearization sysRevDynamic = new NormLinearization(measureingValue, measureingPoint);

        InterpolatingDouble d = sysDynamic.getMap().getInterpolated(new InterpolatingDouble(0.5));
        InterpolatingDouble e = sysRevDynamic.getMap().getInterpolated(d);

        BigDecimal tempnL2 = sysDynamic.calculation(BigDecimal.valueOf(0.5),BigDecimal.valueOf(1));
        assertThat(tempnL2.doubleValue()).isCloseTo(0.7930,Percentage.withPercentage(0.1));

        BigDecimal tempnL4 = sysDynamic.calculation(sysRevDynamic.calculation(BigDecimal.valueOf(0.5),BigDecimal.valueOf(1)), BigDecimal.valueOf(1));
        assertThat(tempnL4.doubleValue()).isCloseTo(0.5,Percentage.withPercentage(0.1));

        for (int i = 1; i<10;i++ ){
            tempnL4 = sysDynamic.calculation(sysRevDynamic.calculation(BigDecimal.valueOf(i*0.1),BigDecimal.valueOf(1)), BigDecimal.valueOf(1));
            assertThat(tempnL4.doubleValue()).isCloseTo(i*0.1,Percentage.withPercentage(0.1));
        }
    }
}