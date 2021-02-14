package com.pipiobjo.brewery.interpolable;

import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

@Slf4j
public class NormLinearization {

    private InterpolatingTreeMap<InterpolatingDouble, InterpolatingDouble> map = new InterpolatingTreeMap<>();

    public NormLinearization(double[] measureingPointInput, double[] measureingValueInput) {
        generateMap(measureingPointInput,measureingValueInput);
    }

    private void generateMap(double[] measureingPointInput, double[] measureingValueInput) {
        // map erstellen
        if (measureingValueInput.length!=measureingPointInput.length){
            log.info("dimension not equal, N1={}", measureingPointInput.length,", N2={}", measureingValueInput.length);
            return;
        }

        int n = measureingValueInput.length;
        InterpolatingDouble[] measureingPoint = new InterpolatingDouble[n];
        InterpolatingDouble[] measureingValue = new InterpolatingDouble[n];

        for (int i = 0; i < n; i++) {
            measureingPoint[i] = new InterpolatingDouble(measureingPointInput[i]);
        }

        for (int i = 0; i < n; i++) {
            measureingValue[i] = new InterpolatingDouble(measureingValueInput[i]);
        }

        for (int i = 0; i < n; i++) {
            // put(key,value) --> swapping map
            map.put(measureingValue[i], measureingPoint[i]);
        }
    }

    public BigDecimal calculation(BigDecimal input, BigDecimal normingFactor) {
        BigDecimal inFun = input.multiply(normingFactor);
        InterpolatingDouble outValue = map.getInterpolated(new InterpolatingDouble(inFun.doubleValue()));

        return BigDecimal.valueOf(outValue.getValue());
    }

    public InterpolatingTreeMap<InterpolatingDouble, InterpolatingDouble> getMap(){
        return map;
    }
}