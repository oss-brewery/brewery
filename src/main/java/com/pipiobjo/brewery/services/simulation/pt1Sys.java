package com.pipiobjo.brewery.services.simulation;

import java.math.BigDecimal;

public class pt1Sys {
    private BigDecimal timeConstant;
    private BigDecimal kGain;
    private BigDecimal X;
    private BigDecimal Xdot;

    public pt1Sys(BigDecimal timeConstant, BigDecimal kGain, BigDecimal initialCondition){
        this.timeConstant = timeConstant;
        this.kGain = kGain;
        this.X = initialCondition;
    }

    public BigDecimal calculate(BigDecimal stepSizeBD, BigDecimal input){
        // PT1 with forward euler
        Xdot = kGain.multiply(input).subtract(X).divide(timeConstant);
        X = X.add(stepSizeBD.multiply(Xdot).divide(BigDecimal.valueOf(1000)));
        return X;
    }
}
