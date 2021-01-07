package com.pipiobjo.brewery.services.collector;

import lombok.extern.slf4j.Slf4j;
import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class PiCalculator {
    private BigDecimal conditionIntegrator = BigDecimal.valueOf(0); // Initial condition

    public BigDecimal calculate(Long stepSize, BigDecimal setpoint, BigDecimal feedback, BigDecimal kp,BigDecimal ki,BigDecimal upperLimit, BigDecimal lowerLimit) {
        // vars
        BigDecimal stepSizeBD = BigDecimal.valueOf(stepSize);
        BigDecimal postSaturation;      // after saturation
        BigDecimal preSum;              // forecast for Anti-Wind-up
        BigDecimal sum;
        BigDecimal controlDifference;
        BigDecimal pGain;

        controlDifference = setpoint.subtract(feedback);

        pGain = controlDifference.multiply(kp);
        preSum = pGain.add(conditionIntegrator);

        // Anti-Wind-up
        if (preSum.compareTo(upperLimit)>=0||preSum.compareTo(lowerLimit)<=0) {
            // upper or lower limit ist reached
        }else{
            // limit NOT reached --> numerical integration - forward euler
            conditionIntegrator = conditionIntegrator.add(stepSizeBD.multiply(controlDifference).multiply(ki).divide(BigDecimal.valueOf(1000)));
        }

        sum = pGain.add(conditionIntegrator);
        // saturation
        if (sum.compareTo(upperLimit)>0) {
            // upper limit ist reached
            postSaturation = upperLimit;
        }else if (sum.compareTo(lowerLimit)<0){
            // lower limit ist reached
            postSaturation = lowerLimit;
        }else{
            // limit NOT reached
            postSaturation = sum;
        }

        return postSaturation;
    }
}

