package com.pipiobjo.brewery.services.simulation;

import com.pipiobjo.brewery.interpolable.InterpolatingDouble;
import com.pipiobjo.brewery.interpolable.NormLinearization;
import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.isNull;


@Data
@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class BreweryHardwareSimulation {
    /**
     * Information
     * - all temperature variables have the unit °C, if not declared different (e.g. Kelvin)
     * - variables are normally given as SI units
     *
     */

    // define calculation constants
    public static final long SIMULATION_TICK_TIME_MS = 150;

    public static final BigDecimal DIFFERENCE_KELVIN_CELSIUS = BigDecimal.valueOf(273);

    public static final BigDecimal MIN_INCREMENTS_MOTOR = BigDecimal.valueOf(0);
    public static final BigDecimal MAX_INCREMENTS_MOTOR = BigDecimal.valueOf(5L*200L);

    private static final BigDecimal THERMAL_RESISTOR_AIR_2_BREW = BigDecimal.valueOf(0.0005);
    private static final BigDecimal THERMAL_RESISTOR_BURNER_2_BREW = BigDecimal.valueOf(0.0035);

    private static final BigDecimal WEIGHT_WATER = BigDecimal.valueOf(55);
    private static final BigDecimal WEIGHT_CEREALS = BigDecimal.valueOf(40);

    private static final BigDecimal THERMAL_CAPACITY_COEFFICIENT_WATER = BigDecimal.valueOf(4184);
    private static final BigDecimal THERMAL_CAPACITY_COEFFICIENT_CEREALS = BigDecimal.valueOf(1500);
    private static final BigDecimal THERMAL_CAPACITY_WATER = THERMAL_CAPACITY_COEFFICIENT_WATER.multiply(WEIGHT_WATER);
    private static final BigDecimal THERMAL_CAPACITY_CEREALS = THERMAL_CAPACITY_COEFFICIENT_CEREALS.multiply(WEIGHT_CEREALS);
    private static final BigDecimal THERMAL_CAPACITY_BREW = THERMAL_CAPACITY_WATER.add(THERMAL_CAPACITY_CEREALS);

    private static final BigDecimal TIME_CONSTANT_FLAME_TEMP = BigDecimal.valueOf(3);
    private static final BigDecimal TIME_CONSTANT_IN_POT_1 = THERMAL_CAPACITY_BREW.multiply(THERMAL_RESISTOR_AIR_2_BREW);
    private static final BigDecimal GAIN_FLAME_TEMP = BigDecimal.valueOf(1);
    private static final BigDecimal GAIN_IN_POT_1 = BigDecimal.valueOf(1);
    private static final BigDecimal INITIAL_BURNER_TEMP = BigDecimal.valueOf(600);
    private static final BigDecimal INITIAL_CONDITION_FLAME_TEMP_SENSOR = BigDecimal.valueOf(600);
    private static final BigDecimal INITIAL_BREW_TEMP = BigDecimal.valueOf(30);
    private static final BigDecimal INITIAL_CONDITION_IN_POT_1 = INITIAL_BREW_TEMP.add(DIFFERENCE_KELVIN_CELSIUS).divide(THERMAL_RESISTOR_AIR_2_BREW);

    private static final BigDecimal MAX_BURNER_TEMP = BigDecimal.valueOf(1200);
    private static final BigDecimal MIN_BURNER_TEMP = BigDecimal.valueOf(0);

    private NormLinearization normLinearization = null;
    private NormLinearization normLinearizationRevers = null;
    private BigDecimal incrementsMotor = BigDecimal.valueOf(0);

    private BigDecimal controlCabinetAirTemp = BigDecimal.ZERO;
    private BigDecimal airTemp = BigDecimal.ZERO;
    private BigDecimal flameTemp = BigDecimal.ZERO;
    private BigDecimal flameTempSensor = BigDecimal.ZERO;
    private BigDecimal inPotTempBottom = BigDecimal.ZERO;
    private BigDecimal inPotTempMiddle = BigDecimal.ZERO;
    private BigDecimal inPotTempTop = BigDecimal.ZERO;
    private BigDecimal brewTempKelvin = INITIAL_BREW_TEMP.add(DIFFERENCE_KELVIN_CELSIUS);
    private BigDecimal burnerTempKelvin = INITIAL_BURNER_TEMP.add(DIFFERENCE_KELVIN_CELSIUS);
    private boolean automaticGasBurnerIsOn = true;

    private Pt1Sys pt1SysCabinetAirTemp = new Pt1Sys( BigDecimal.valueOf(15), BigDecimal.valueOf(1), BigDecimal.valueOf(0));
    private Pt1Sys pt1SysAirTemp = new Pt1Sys(BigDecimal.valueOf(20), BigDecimal.valueOf(1), BigDecimal.valueOf(0));
    private Pt1Sys pt1SysInPot = new Pt1Sys(TIME_CONSTANT_IN_POT_1, GAIN_IN_POT_1, INITIAL_CONDITION_IN_POT_1);
    private Pt1Sys pt1SysFlameTempSensor = new Pt1Sys(TIME_CONSTANT_FLAME_TEMP, GAIN_FLAME_TEMP, INITIAL_CONDITION_FLAME_TEMP_SENSOR);

    // for manual setting values
    private BigDecimal tempAmbientAirCelsius = BigDecimal.valueOf(19.5);
    private BigDecimal tempAmbientAirKelvin = tempAmbientAirCelsius.add(DIFFERENCE_KELVIN_CELSIUS);
    private BigDecimal inputCabinetAirTemp = BigDecimal.ZERO;
    private BigDecimal inputAirTemp = BigDecimal.ZERO;
    private int cycleCount = 0;
    private int cycleCountModulo = 1000;
    private int maxRandom = 100;
    private int minRandom = 0;
    private int randomNumber;

    // TODO just for debugging at the moment --> try to find a future solution
    private BigDecimal inputRateOfHeatFlow1 = BigDecimal.ZERO;

    private Cancellable cancellable;

    @PostConstruct
    void init() {
        log.info("simulation is starting with tick time: {}ms", SIMULATION_TICK_TIME_MS);

        initLinearization();

        Multi<Long> ticks = Multi.createFrom().ticks().every(Duration.ofMillis(SIMULATION_TICK_TIME_MS));
        if(this.cancellable != null){
            log.info("simulation is already running");
        }

        this.cancellable = ticks.subscribe().with( it -> {
            this.calculate(BigDecimal.valueOf(SIMULATION_TICK_TIME_MS));
        });

    }

    @PreDestroy
    void preDestroy() {
        log.info("pre destroy for BreweryHardwareSimulation");
        if (cancellable != null) {
            cancellable.cancel();
            log.info("simulation stopped");
        } else {
            log.info("nothing to stop");
        }

    }

    public BigDecimal getDebugValue1(){
        return burnerTempKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
    }

    public BigDecimal getDebugValue2(){
        return inputRateOfHeatFlow1;
    }

    private void calculate(BigDecimal stepSizeBD) {
        calculateCabinetAirTemp(stepSizeBD, inputCabinetAirTemp);
        calculateAirTemp(stepSizeBD, inputAirTemp);
        calculateBurnerTempKelvin();
        calculateFlameTemp();
        calculateInPot(stepSizeBD, flameTemp.add(DIFFERENCE_KELVIN_CELSIUS));
        calculateFlameTempSensor(stepSizeBD, flameTemp);

        // manual setting values
        if (cycleCount % cycleCountModulo == 0) {
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            inputAirTemp = BigDecimal.valueOf(randomNumber);
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            inputCabinetAirTemp = BigDecimal.valueOf(randomNumber);
//            setFlameIsOn(!isFlameIsOn());
        }
        cycleCount++;
    }


    private void calculateCabinetAirTemp(BigDecimal stepSizeBD, BigDecimal input) {
        pt1SysCabinetAirTemp.calculate(stepSizeBD, input);
        controlCabinetAirTemp = pt1SysCabinetAirTemp.getX();
    }

    private void calculateAirTemp(BigDecimal stepSizeBD, BigDecimal inputCelsius)
    {
        pt1SysAirTemp.calculate(stepSizeBD, inputCelsius);
        airTemp = pt1SysAirTemp.getX();
    }

    private void calculateFlameTemp(){
        if (automaticGasBurnerIsOn){
            flameTemp = burnerTempKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        }
        else{
            flameTemp = tempAmbientAirKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        }
    }

    private void calculateFlameTempSensor(BigDecimal stepSizeBD, BigDecimal inputCelsius)
    {
        pt1SysFlameTempSensor.calculate(stepSizeBD, inputCelsius);
        flameTempSensor = pt1SysFlameTempSensor.getX();
    }

    private void calculateInPot(BigDecimal stepSizeBD, BigDecimal inputFlameTempKelvin)
    {
        inputRateOfHeatFlow1 = (inputFlameTempKelvin.subtract(brewTempKelvin)).divide(THERMAL_RESISTOR_BURNER_2_BREW,15, RoundingMode.HALF_DOWN);
        BigDecimal inputRateOfHeatFlow2 = tempAmbientAirKelvin.divide(THERMAL_RESISTOR_AIR_2_BREW,15, RoundingMode.HALF_DOWN);
        BigDecimal inputRateOfHeatFlow = inputRateOfHeatFlow1.add(inputRateOfHeatFlow2);
        pt1SysInPot.calculate(stepSizeBD, inputRateOfHeatFlow);
        brewTempKelvin = pt1SysInPot.getX().multiply(THERMAL_RESISTOR_AIR_2_BREW);

        inPotTempBottom = brewTempKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS).add(BigDecimal.valueOf(-1));
        inPotTempMiddle = brewTempKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        inPotTempTop = brewTempKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS).add(BigDecimal.valueOf(1));
    }

    private void calculateBurnerTempKelvin(){
        burnerTempKelvin = BigDecimal.valueOf(
                normLinearizationRevers.getMap().getInterpolated(
                        new InterpolatingDouble( 100.0*getIncrementsMotor().doubleValue()/MAX_INCREMENTS_MOTOR.doubleValue() )).getValue());
    }

    private void initLinearization() {

        if (isNull(normLinearization) && isNull(normLinearizationRevers)){

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
