package com.pipiobjo.brewery.services.controller;

import com.pipiobjo.brewery.adapters.monotorcontrol.MotorControlAdapter;
import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
import com.pipiobjo.brewery.services.model.CollectionResult;
import io.quarkus.runtime.configuration.ProfileManager;
import io.quarkus.vertx.ConsumeEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Slf4j
@ApplicationScoped
public class GeneralControllerCycle {

    // Testing Values for Simulation
    private BigDecimal setPointInPotTemp = BigDecimal.valueOf(40);
    private int cycleCount = 0;
    private int cycleCountModulo = 500;

    private BigDecimal motorMovePercent = BigDecimal.ZERO;

    @Inject
    BurnerController burnerController;
    @Inject
    InPotTempController inPotTempController;
    @Inject
    MotorControlAdapter motorControlAdapter;
    @Inject
    SensorCollectorServiceConfigProperties config;

    CollectionResult collectionResult = null;

    @ConsumeEvent(value = SensorCollectorService.PUBLISH_TO_CALCULATION_EVENT_NAME, blocking = true)
    public void receiveData(CollectionResult event){
        collectionResult = event;
        calculate(BigDecimal.valueOf(config.getCalculationIntervallInMS()));  // TODO discussion on implementing entry point
    }

    // TODO implement postConstructor

    public void calculate(BigDecimal stepSizeBD) {
        // calculation cycle for controller
        if (ProfileManager.getActiveProfile().equals("mockDevices") && collectionResult != null && isPresent(collectionResult)){

            BigDecimal setPointBurnerTemp = inPotControllerFunction(
                    stepSizeBD, setPointInPotTemp, collectionResult.getInpotTemperature().getBottom().orElse(BigDecimal.ZERO));

            motorMovePercent = burnerControllerFunction(
                    stepSizeBD, setPointBurnerTemp,
                    collectionResult.getFlameTemperature().getTemperature().orElse(BigDecimal.ZERO),
                    feedForward(collectionResult.getControlCabinetTemperature().getAirTemp().orElse(BigDecimal.ZERO)));
//            motorControlAdapter.moveToPercent(motorMovePercent);

            // manual testing
            if (cycleCount % cycleCountModulo == 0) {
                if (setPointInPotTemp.equals(BigDecimal.valueOf(40))){
                    setPointInPotTemp = BigDecimal.valueOf(35);
                }else
                {
                    setPointInPotTemp = BigDecimal.valueOf(40);
                }
            }
            cycleCount++;
//            setPointInPotTemp = BigDecimal.valueOf(19.5);  // TODO prof differential equation
        }
    }

    private BigDecimal inPotControllerFunction(BigDecimal stepSizeBD, BigDecimal setPoint, BigDecimal feedback){
        inPotTempController.calculate(stepSizeBD, setPoint, feedback); // TODO find a way to use all temps
         return inPotTempController.getControllerOutputCelsius();
    }

    private BigDecimal burnerControllerFunction(BigDecimal stepSizeBD, BigDecimal setPoint, BigDecimal feedback, BigDecimal feedForwardInput){
         burnerController.calculate(stepSizeBD, setPoint, feedback, feedForwardInput);
         return burnerController.getBurnerControllerOutputPercent();
         // TODO output is different to "inPotControllerFunction" --> redesign motorControlAdapter in the future in alignment to the other controllers
    }

    private BigDecimal feedForward(BigDecimal disturbanceCelsius){
        BigDecimal thermalResistorAir2Brew = BigDecimal.valueOf(0.0005);
        BigDecimal thermalResistorBurner2Brew = BigDecimal.valueOf(0.0035);
        return disturbanceCelsius.multiply(BigDecimal.valueOf(-1)).multiply(thermalResistorBurner2Brew).divide(thermalResistorAir2Brew, 15, RoundingMode.HALF_DOWN);
    }

    private boolean isPresent(CollectionResult event){
        if (event.getFlameTemperature().getTemperature().isPresent()){
            return true;
        }else {
            return false;
        }
    }
}
