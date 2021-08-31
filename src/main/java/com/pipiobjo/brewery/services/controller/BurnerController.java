package com.pipiobjo.brewery.services.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Slf4j
@ApplicationScoped
public class BurnerController {

    // values to config
    private static final BigDecimal TIME_CONSTANT_SENSOR = BigDecimal.valueOf(3);
    private static final BigDecimal BAND_WIDTH_DIV = BigDecimal.valueOf(2); // greater than 2!

    private static final BigDecimal PERCENT = BigDecimal.valueOf(100); // [%]
    private static final BigDecimal MAX_BURNER_TEMP = BigDecimal.valueOf(1200); // [°C]
    private static final BigDecimal NORMALIZATION_FACTOR = PERCENT.divide(MAX_BURNER_TEMP, 15, RoundingMode.HALF_DOWN) ; // [%/°C]
//    private static final BigDecimal NORMALIZATION_FACTOR = BigDecimal.ONE ; // [%/°C]

    private BigDecimal setPoint = BigDecimal.valueOf(0);
    private BigDecimal feedback = BigDecimal.valueOf(0);
    private BigDecimal manipulatedVariable = BigDecimal.valueOf(0);

    private BigDecimal kp = BigDecimal.ONE.divide(BAND_WIDTH_DIV, 15, RoundingMode.HALF_DOWN).multiply(NORMALIZATION_FACTOR);
    private BigDecimal ki = kp.divide(TIME_CONSTANT_SENSOR, 15, RoundingMode.HALF_DOWN);

    private BigDecimal upperLimit = BigDecimal.valueOf(1200).divide(NORMALIZATION_FACTOR, 15, RoundingMode.HALF_DOWN);
    private BigDecimal lowerLimit = BigDecimal.valueOf(0).divide(NORMALIZATION_FACTOR, 15, RoundingMode.HALF_DOWN);

    private PiCalculator piCalculator = new PiCalculator();

    public void calculate(BigDecimal stepSizeBD){
        manipulatedVariable = piCalculator.calculate(stepSizeBD, setPoint, feedback, kp, ki,upperLimit, lowerLimit);
        manipulatedVariable = manipulatedVariable.divide(NORMALIZATION_FACTOR, 15, RoundingMode.HALF_DOWN);
    }

}
