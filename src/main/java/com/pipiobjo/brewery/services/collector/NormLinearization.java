package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.interpolable.InterpolatingDouble;
import com.pipiobjo.brewery.interpolable.InterpolatingTreeMap;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NormLinearization {
    // make a linearization with f^-1
    BigDecimal temp = BigDecimal.valueOf(0);
    private static final double start = 0;
    private static final double end = 10;
    public BigDecimal TestValue = BigDecimal.valueOf(0);
    private double[] measureingPoint;
    private double[] measureingValue;
    private int N;

    InterpolatingTreeMap<InterpolatingDouble, InterpolatingDouble> map = new InterpolatingTreeMap<>();


    // constructor
    public NormLinearization(int N) {
        // N --> data points
        InterpolatingDouble[] measureingPoint = new InterpolatingDouble[N];
        this.N = N;
        for (int i = (int) start; i < N; i++) {
            measureingPoint[i] = new InterpolatingDouble(i * (end - start) / (N - 1));
        }

        // measuring value --> just an example function
        InterpolatingDouble[] measureingValue = new InterpolatingDouble[N];
        for (int i = (int) start; i < N; i++) {
            measureingValue[i] = new InterpolatingDouble(Math.pow(measureingPoint[i].getValue(), 2));
        }

        // map erstellen
        for (int i = (int) start; i < N; i++) {
            // put(key,value) --> swapping map
            map.put(measureingValue[i], measureingPoint[i]);
        }

    }

    public BigDecimal calculation(BigDecimal input, BigDecimal normingFactor) {
        temp = input.multiply(normingFactor);
        /*
        for(int i = (int)start; i < N; i++){
            measureingValue[i].compareTo();
        }*/

        return temp;
    }


}
