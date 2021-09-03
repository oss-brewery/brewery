package com.pipiobjo.brewery.services.controller;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Slf4j
@ApplicationScoped
public class InPotTempController {

    private static final BigDecimal THERMAL_CAPACITY_COEFFICIENT_WATER = BigDecimal.valueOf(4184);
    private static final BigDecimal THERMAL_CAPACITY_COEFFICIENT_CEREALS = BigDecimal.valueOf(1500);

    private static final BigDecimal MAX_BURNER_TEMP = BigDecimal.valueOf(1000);
    private static final BigDecimal MIN_BURNER_TEMP = BigDecimal.valueOf(0);

    private static final BigDecimal DESIRED_TIME_CONSTANT = BigDecimal.valueOf(10);

    private BigDecimal controllerOutputCelsius = BigDecimal.ZERO;

    private PiCalculator piCalculator = new PiCalculator();

    private BigDecimal kp;
    private BigDecimal ki;

    @PostConstruct
    void init() {
        log.info("In pot temperature controller is starting");
        calculatePICoefficients(DESIRED_TIME_CONSTANT);
        log.info("Parameters of the pot controller - KP = {} ; KI = {}", kp, ki);
    }

    public void calculate(BigDecimal stepSizeBD, BigDecimal setPoint, BigDecimal feedback){
        controllerOutputCelsius = piCalculator.calculate(stepSizeBD, setPoint, feedback, kp, ki, MAX_BURNER_TEMP, MIN_BURNER_TEMP);
    }


    private void calculatePICoefficients(BigDecimal desiredTimeConstant){
        // Properties of the theoretical system
        BigDecimal thermalResistorAir2Brew = BigDecimal.valueOf(0.0005);
        BigDecimal thermalResistorBurner2Brew = BigDecimal.valueOf(0.0035);
        BigDecimal weightWater = BigDecimal.valueOf(55);
        BigDecimal weightCereals = BigDecimal.valueOf(40);

        // calculation
        BigDecimal thermalCapacityWater = THERMAL_CAPACITY_COEFFICIENT_WATER.multiply(weightWater);
        BigDecimal thermalCapacityCereals = THERMAL_CAPACITY_COEFFICIENT_CEREALS.multiply(weightCereals);
        BigDecimal thermalCapacityBrew = thermalCapacityWater.add(thermalCapacityCereals);

        // desire behavior
        BigDecimal omegaDesired =  BigDecimal.ONE.divide(desiredTimeConstant, 15, RoundingMode.HALF_DOWN);

        // Calculate
        BigDecimal timeConstantPot = (thermalCapacityBrew.multiply(thermalResistorAir2Brew).multiply(thermalResistorBurner2Brew))
                .divide(thermalResistorAir2Brew.add(thermalResistorBurner2Brew), 15, RoundingMode.HALF_DOWN );
        BigDecimal pGainPot = thermalResistorAir2Brew
                .divide(thermalResistorAir2Brew.add(thermalResistorBurner2Brew), 15, RoundingMode.HALF_DOWN );

        kp = omegaDesired.multiply(timeConstantPot).divide(pGainPot, 15, RoundingMode.HALF_DOWN);
        ki = kp.divide(timeConstantPot ,15 , RoundingMode.HALF_DOWN);
    }

}
