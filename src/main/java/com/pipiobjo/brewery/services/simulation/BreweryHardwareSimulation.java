package com.pipiobjo.brewery.services.simulation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class BreweryHardwareSimulation {


    @Getter private static BigDecimal controlCabinetAirTemp = BigDecimal.ZERO;
    @Getter private static BigDecimal airTemp = BigDecimal.ZERO;
    @Getter private static BigDecimal flameTemp = BigDecimal.ZERO;
    @Getter private static BigDecimal inPotTempBottom = BigDecimal.ZERO;
    @Getter private static BigDecimal inPotTempMiddle = BigDecimal.ZERO;
    @Getter private static BigDecimal inPotTempTop = BigDecimal.ZERO;

    @Getter @Setter private static boolean flameIsOn = false;

    private static BigDecimal tempBrewKelvin = BigDecimal.valueOf(275);
    private static BigDecimal tempBurnerKelvin = BigDecimal.valueOf(900);

    private static final BigDecimal DIFFERENCE_KELVIN_CELSIUS = BigDecimal.valueOf(273);
    private static final BigDecimal THERMAL_RESISTOR_AIR_2_BREW = BigDecimal.valueOf(0.0005);
    private static final BigDecimal THERMAL_RESISTOR_BURNER_2_BREW = BigDecimal.valueOf(0.0035);
    private static final BigDecimal WEIGHT_WATER = BigDecimal.valueOf(55);
    private static final BigDecimal WEIGHT_CEREALS = BigDecimal.valueOf(40);
    private static final BigDecimal THERMAL_CAPACITY_COEFFICIENT_WATER = BigDecimal.valueOf(4184);
    private static final BigDecimal THERMAL_CAPACITY_COEFFICIENT_CEREALS = BigDecimal.valueOf(1500);
    private static final BigDecimal THERMAL_CAPACITY_WATER = THERMAL_CAPACITY_COEFFICIENT_WATER.multiply(WEIGHT_WATER);
    private static final BigDecimal THERMAL_CAPACITY_CEREALS = THERMAL_CAPACITY_COEFFICIENT_CEREALS.multiply(WEIGHT_CEREALS);
    private static final BigDecimal THERMAL_CAPACITY_BREW = THERMAL_CAPACITY_WATER.add(THERMAL_CAPACITY_CEREALS);

    private static Pt1Sys pt1SysCabinetAirTemp = new Pt1Sys(
            BigDecimal.valueOf(15), BigDecimal.valueOf(1), BigDecimal.valueOf(0));

    private static Pt1Sys pt1SysAirTemp = new Pt1Sys(
            BigDecimal.valueOf(20), BigDecimal.valueOf(1), BigDecimal.valueOf(0));

    private static Pt1Sys pt1SysInPot = new Pt1Sys(
            BigDecimal.valueOf(100), BigDecimal.valueOf(1), tempBrewKelvin.divide(THERMAL_RESISTOR_AIR_2_BREW));

    // for manual setting values
    private static BigDecimal tempAmbientAirKelvin = BigDecimal.valueOf(290);
    private static BigDecimal inputCabinetAirTemp = BigDecimal.ZERO;
    private static BigDecimal inputAirTemp = BigDecimal.ZERO;
    private static int cycleCount = 0;
    private static int cycleCountModulo = 300;
    private static int maxRandom = 100;
    private static int minRandom = 0;
    private static int randomNumber;

    private BreweryHardwareSimulation(){}

    private static void calculateCabinetAirTemp(BigDecimal stepSizeBD, BigDecimal input)
    {
        pt1SysCabinetAirTemp.calculate(stepSizeBD, input);
        controlCabinetAirTemp = pt1SysCabinetAirTemp.getX();
    }

    private static void calculateAirTemp(BigDecimal stepSizeBD, BigDecimal input)
    {
        pt1SysAirTemp.calculate(stepSizeBD, input);
        airTemp = pt1SysAirTemp.getX();
    }

    private static void calculateInPot(BigDecimal stepSizeBD, BigDecimal input)
    {
        BigDecimal inputCalc = tempAmbientAirKelvin.divide(THERMAL_RESISTOR_AIR_2_BREW);
        inputCalc = inputCalc.add(input);
        inputCalc = inputCalc.subtract(tempBrewKelvin);
        pt1SysInPot.calculate(stepSizeBD, inputCalc);
        tempBrewKelvin = pt1SysInPot.getX().multiply(THERMAL_RESISTOR_AIR_2_BREW);

        inPotTempBottom = tempBrewKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS).add(BigDecimal.valueOf(-1));
        inPotTempMiddle = tempBrewKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        inPotTempTop = tempBrewKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS).add(BigDecimal.valueOf(1));
    }

    private static void calculateFlameTemp()
    {
        if (flameIsOn){
            flameTemp = tempBurnerKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        }
        else{
            flameTemp = tempAmbientAirKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        }

    }

    public static void calculate(BigDecimal stepSizeBD){
        calculateCabinetAirTemp(stepSizeBD, inputCabinetAirTemp);
        calculateAirTemp(stepSizeBD, inputAirTemp);
        calculateInPot(stepSizeBD, tempBurnerKelvin);
        calculateFlameTemp();

        // manual setting values
        if (cycleCount % cycleCountModulo == 0){
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            inputAirTemp = BigDecimal.valueOf(randomNumber);
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            inputCabinetAirTemp = BigDecimal.valueOf(randomNumber);
            setFlameIsOn(!isFlameIsOn());
        }
        cycleCount++;
    }


}
