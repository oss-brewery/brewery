package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.interpolable.InterpolatingDouble;
import com.pipiobjo.brewery.interpolable.InterpolatingTreeMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
public class NormLinearization {
    private BigDecimal inFun;
    private InterpolatingDouble[] measureingPoint;
    private InterpolatingDouble[] measureingValue;
    private int n;

    InterpolatingTreeMap<InterpolatingDouble, InterpolatingDouble> map = new InterpolatingTreeMap<>();

    public NormLinearization(double[] measureingPointInput, double[] measureingValueInput) {
        setMap(measureingPointInput,measureingValueInput);
    }

    private void setMap(double[] measureingPointInput, double[] measureingValueInput) {
        // map erstellen
        if (measureingValueInput.length!=measureingPointInput.length){
            log.info("dimension not equal, N1={}", measureingPointInput.length,", N2={}", measureingValueInput.length);
            return;
        }

        n = measureingValueInput.length;
        measureingPoint = new InterpolatingDouble[n];
        measureingValue = new InterpolatingDouble[n];

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
        inFun = input.multiply(normingFactor);
        InterpolatingDouble outValue = getMap().getInterpolated(new InterpolatingDouble(inFun.doubleValue()));

        return BigDecimal.valueOf(outValue.getValue());
    }
}
