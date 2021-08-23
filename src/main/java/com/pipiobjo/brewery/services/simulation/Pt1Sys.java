package com.pipiobjo.brewery.services.simulation;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class Pt1Sys {
    private BigDecimal timeConstant;
    private BigDecimal kGain;
    private BigDecimal x;

    public Pt1Sys(BigDecimal timeConstant, BigDecimal kGain, BigDecimal initialCondition){
        this.timeConstant = timeConstant;
        this.kGain = kGain;
        this.x = initialCondition;
    }

    public BigDecimal calculate(BigDecimal stepSizeBD, BigDecimal input){
        BigDecimal xdot;
        // PT1 with forward euler
        xdot = kGain.multiply(input).subtract(x).divide(timeConstant, 2, RoundingMode.HALF_DOWN);
        x = x.add(stepSizeBD.multiply(xdot).divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_DOWN));
        return x;
    }
}
