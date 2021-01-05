package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class PiCalculator {

    private BigDecimal ConditionIntegrator = BigDecimal.valueOf(0); // Initial condition
    private BigDecimal sum;                 // pGain and integration
    private BigDecimal postSaturation;      // after saturation
    private BigDecimal controlDifference;
    private BigDecimal pGain;
    private BigDecimal stepSizeBD;
    private BigDecimal preSum;              // forecast for Anti-Wind-up

    public BigDecimal calculate(Long stepSize, BigDecimal setpoint, BigDecimal feedback, BigDecimal KP,BigDecimal KI,BigDecimal upperLimit, BigDecimal lowerLimit) {
        // conversion
        stepSizeBD = BigDecimal.valueOf(stepSize);

        controlDifference = setpoint.subtract(feedback);

        pGain = controlDifference.multiply(KP);
        preSum = pGain.add(ConditionIntegrator);

        // Anti-Wind-up
        if (preSum.compareTo(upperLimit)>=0|preSum.compareTo(lowerLimit)<=0) {
            // upper or lower limit ist reached
            ConditionIntegrator = ConditionIntegrator;
        }else{
            // limit NOT reached --> numerical integration - forward euler
            ConditionIntegrator = ConditionIntegrator.add(stepSizeBD.multiply(controlDifference).multiply(KI).divide(BigDecimal.valueOf(1000)));
        }

        sum = pGain.add(ConditionIntegrator);
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
