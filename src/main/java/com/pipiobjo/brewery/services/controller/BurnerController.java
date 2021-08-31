package com.pipiobjo.brewery.services.controller;

import lombok.Data;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
@Data
public class BurnerController {

    private static final BigDecimal TIME_CONSTANT_SENSOR = BigDecimal.valueOf(3);
    private static final BigDecimal BAND_WIDTH_DIV = BigDecimal.valueOf(2); // greater than 2!
    private static final BigDecimal PERCENT = BigDecimal.valueOf(100); // [%]
    private static final BigDecimal MAX_BURNER_TEMP = BigDecimal.valueOf(1200); // [°C]
    private static final BigDecimal NORMALIZATION_FACTOR = PERCENT.divide(MAX_BURNER_TEMP, 15, RoundingMode.HALF_DOWN) ; // [%/°C]

    private PiCalculator piCalculator = new PiCalculator();
    private BigDecimal setPoint = BigDecimal.valueOf(0);
    private BigDecimal feedback = BigDecimal.valueOf(0);
    private BigDecimal kp = BAND_WIDTH_DIV.multiply(NORMALIZATION_FACTOR);
    private BigDecimal ki = kp.divide(TIME_CONSTANT_SENSOR, 15, RoundingMode.HALF_DOWN);
    private BigDecimal upperLimit = BigDecimal.valueOf(1000);
    private BigDecimal lowerLimit = BigDecimal.valueOf(300);

    private BigDecimal manipulatedVariable = BigDecimal.valueOf(0);

    public void calculate(BigDecimal stepSizeBD){
        manipulatedVariable = piCalculator.calculate(stepSizeBD, setPoint, feedback, kp, ki,upperLimit, lowerLimit);
    }

}
