package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import lombok.extern.slf4j.Slf4j;
import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.HashMap;


public class normLinearization {
    // make a linearization with f^-1
    BigDecimal temp = BigDecimal.valueOf(0);
    private static final double start = 0.0;
    private static final double end = 10.0;
    public BigDecimal TestValue = BigDecimal.valueOf(0);
    private double[] measureingPoint;
    private double[] measureingValue;
    private int N;

    HashMap map = new HashMap();


    // constructor
    public normLinearization(int N){
        // N --> data points
        double[] measureingPoint = new double[N];
        this.N=N;
        for(int i = (int)start; i < N; i++){
            measureingPoint[i]= (double)i*(end-start)/(double)(N-1);
        }

        // measuring value --> just an example function
        double[] measureingValue = new double[N];
        for(int i = (int)start; i < N; i++){
            measureingValue[i] = Math.pow(measureingPoint[i],2);
        }

        // map erstellen
        for(int i = (int)start; i < N; i++){
            // put(key,value) --> swapping map
            map.put(measureingValue[i],measureingPoint[i]);
        }

    }

    public BigDecimal calculation(BigDecimal input, BigDecimal normingFactor){
        temp = input.multiply(normingFactor);
        /*
        for(int i = (int)start; i < N; i++){
            measureingValue[i].compareTo();
        }*/
        
        return temp;
    }



}
