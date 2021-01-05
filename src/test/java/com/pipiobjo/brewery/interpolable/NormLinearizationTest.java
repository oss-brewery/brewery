package com.pipiobjo.brewery.interpolable;

import com.pipiobjo.brewery.interpolable.InterpolatingDouble;
import com.pipiobjo.brewery.services.collector.NormLinearization;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class NormLinearizationTest {

    @Test
    void calculation() {
        int N = 101;
        NormLinearization nL = new NormLinearization(N);

        log.info("normalisation = {}", nL);

        InterpolatingDouble a = nL.getMap().getInterpolated(new InterpolatingDouble(0.25));
        assertThat(a.getValue()).isEqualTo(0.5);

        InterpolatingDouble b = nL.getMap().getInterpolated(new InterpolatingDouble(0.36));
        assertThat(b.getValue()).isEqualTo(0.6);

        InterpolatingDouble c = nL.getMap().getInterpolated(new InterpolatingDouble(0.3));
        assertThat(c).isNotNull();
        assertThat(c.getValue()).isCloseTo(0.5454545454545454, Percentage.withPercentage(0.000001));

    }
}