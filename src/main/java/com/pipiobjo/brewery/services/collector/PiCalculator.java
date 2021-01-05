package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class PiCalculator {

    private static BigDecimal ConditionIntegrator = BigDecimal.valueOf(0);

    public BigDecimal calculate(Long stepSize, BigDecimal targetTemp, BigDecimal flameTemp) {
        // conversion
        BigDecimal stepSizeBD = BigDecimal.valueOf(stepSize);

        // numerical integration - forward euler
        BigDecimal controlDifference = targetTemp.subtract(flameTemp);
        BigDecimal tempSum = stepSizeBD.multiply(controlDifference).divide(BigDecimal.valueOf(1000));
        ConditionIntegrator = ConditionIntegrator.add(tempSum);


        return ConditionIntegrator;
    }
}
