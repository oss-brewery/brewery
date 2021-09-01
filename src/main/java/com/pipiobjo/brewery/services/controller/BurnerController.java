package com.pipiobjo.brewery.services.controller;

import com.pipiobjo.brewery.adapters.monotorcontrol.MotorControlAdapter;
import com.pipiobjo.brewery.interpolable.InterpolatingDouble;
import com.pipiobjo.brewery.interpolable.NormLinearization;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.isNull;

@Data
@Slf4j
@ApplicationScoped
public class BurnerController {

    // TODO values to config
    private static final BigDecimal TIME_CONSTANT_SENSOR = BigDecimal.valueOf(3);
    private static final BigDecimal BAND_WIDTH_DIV = BigDecimal.valueOf(0.5); // shall be greater than 2!

    private static final BigDecimal MAX_BURNER_TEMP = BigDecimal.valueOf(1200);
    private static final BigDecimal MIN_BURNER_TEMP = BigDecimal.valueOf(0);

    private BigDecimal kp = BigDecimal.ONE.divide(BAND_WIDTH_DIV, 15, RoundingMode.HALF_DOWN);
    private BigDecimal ki = kp.divide(TIME_CONSTANT_SENSOR, 15, RoundingMode.HALF_DOWN);

    private PiCalculator piCalculator = new PiCalculator();

    private NormLinearization normLinearization = null;
    private NormLinearization normLinearizationRevers = null;

    @Inject
    MotorControlAdapter motorControlAdapter;

    @PostConstruct
    public void init() {
        log.info("burner controller is starting");
        initLinearization();
    }

    public void calculate(BigDecimal stepSizeBD, BigDecimal setPoint, BigDecimal feedback){
        BigDecimal burnerControllerOutputCelsius = piCalculator.calculate(stepSizeBD, setPoint, feedback, kp, ki,MAX_BURNER_TEMP, MIN_BURNER_TEMP);
        BigDecimal burnerControllerOutputPercent = BigDecimal.valueOf(
                (normLinearization.getMap().getInterpolated(new InterpolatingDouble(burnerControllerOutputCelsius.doubleValue()))).getValue());

        motorControlAdapter.moveToPercent(burnerControllerOutputPercent);
    }

    private void initLinearization(){

        if (isNull(normLinearization)){

            int numberMeasuringPoints = 101;
            double startPercent = 0;
            double endPercent = 100;
            double endTemperature = MAX_BURNER_TEMP.doubleValue();
            double powValue = 0.5;

            double endNormalizationFactor = endTemperature / Math.pow(endPercent, powValue);

            // creating "measured" points
            double[] measuringPoint = new double[numberMeasuringPoints];
            for (int i = (int) startPercent; i < numberMeasuringPoints; i++) {
                measuringPoint[i] =  (i * (endPercent - startPercent) / (numberMeasuringPoints - 1));
            }
            // creating "measured" values
            double[] measuringValue = new double[numberMeasuringPoints];
            for (int i = (int) startPercent; i < numberMeasuringPoints; i++) {
                measuringValue[i] = endNormalizationFactor * Math.pow(measuringPoint[i], powValue);
            }
            normLinearization = new NormLinearization(measuringPoint, measuringValue);
            normLinearizationRevers = new NormLinearization(measuringValue, measuringPoint);

        } else {
            log.info("burner controller interpolation already exists");
        }

    }

}
