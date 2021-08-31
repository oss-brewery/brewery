package com.pipiobjo.brewery.services.simulation;

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


@Data
@Slf4j
@IfBuildProfile("mockDevices")
@ApplicationScoped
public class BreweryHardwareSimulation {
    // define calculation constants
    private static final long SIMULATION_TICK_TIME_MS = 100;

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

    // define init values for devices
    private BigDecimal controlCabinetAirTemp = BigDecimal.ZERO;
    private BigDecimal airTemp = BigDecimal.ZERO;
    private BigDecimal flameTemp = BigDecimal.ZERO;
    private BigDecimal inPotTempBottom = BigDecimal.ZERO;
    private BigDecimal inPotTempMiddle = BigDecimal.ZERO;
    private BigDecimal inPotTempTop = BigDecimal.ZERO;
    private boolean flameIsOn = false;
    private BigDecimal tempBrewKelvin = BigDecimal.valueOf(275);
    private BigDecimal tempBurnerKelvin = BigDecimal.valueOf(900);
    private Pt1Sys pt1SysCabinetAirTemp = new Pt1Sys( BigDecimal.valueOf(15), BigDecimal.valueOf(1), BigDecimal.valueOf(0));
    private Pt1Sys pt1SysAirTemp = new Pt1Sys(BigDecimal.valueOf(20), BigDecimal.valueOf(1), BigDecimal.valueOf(0));
    private Pt1Sys pt1SysInPot = new Pt1Sys(BigDecimal.valueOf(100), BigDecimal.valueOf(1), tempBrewKelvin.divide(THERMAL_RESISTOR_AIR_2_BREW));

    // for manual setting values
    private BigDecimal tempAmbientAirKelvin = BigDecimal.valueOf(290);
    private BigDecimal inputCabinetAirTemp = BigDecimal.ZERO;
    private BigDecimal inputAirTemp = BigDecimal.ZERO;
    private int cycleCount = 0;
    private int cycleCountModulo = 1000;
    private int maxRandom = 100;
    private int minRandom = 0;
    private int randomNumber;
    private Cancellable cancellable;

    @PostConstruct
    void init() {
        log.info("simulation is starting with tick time: {}ms", SIMULATION_TICK_TIME_MS);

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


    private void calculate(BigDecimal stepSizeBD) {
        calculateCabinetAirTemp(stepSizeBD, inputCabinetAirTemp);
        calculateAirTemp(stepSizeBD, inputAirTemp);
        calculateInPot(stepSizeBD, flameTemp.add(DIFFERENCE_KELVIN_CELSIUS));
        calculateFlameTemp();

        // manual setting values
        if (cycleCount % cycleCountModulo == 0) {
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            inputAirTemp = BigDecimal.valueOf(randomNumber);
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            inputCabinetAirTemp = BigDecimal.valueOf(randomNumber);
            setFlameIsOn(!isFlameIsOn());
        }
        cycleCount++;
    }


    private void calculateCabinetAirTemp(BigDecimal stepSizeBD, BigDecimal input) {
        pt1SysCabinetAirTemp.calculate(stepSizeBD, input);
        controlCabinetAirTemp = pt1SysCabinetAirTemp.getX();
    }

    private void calculateAirTemp(BigDecimal stepSizeBD, BigDecimal input) {
        pt1SysAirTemp.calculate(stepSizeBD, input);
        airTemp = pt1SysAirTemp.getX();
    }

    private void calculateInPot(BigDecimal stepSizeBD, BigDecimal inputTempBurnerKelvin) {
        BigDecimal inputRateOfHeatFlow1 = inputTempBurnerKelvin.divide(THERMAL_RESISTOR_BURNER_2_BREW, 15, RoundingMode.HALF_DOWN);
        BigDecimal inputRateOfHeatFlow2 = tempAmbientAirKelvin.divide(THERMAL_RESISTOR_AIR_2_BREW, 15, RoundingMode.HALF_DOWN);
        BigDecimal inputRateOfHeatFlow = inputRateOfHeatFlow1.add(inputRateOfHeatFlow2);
        pt1SysInPot.calculate(stepSizeBD, inputRateOfHeatFlow);
        tempBrewKelvin = pt1SysInPot.getX().multiply(THERMAL_RESISTOR_AIR_2_BREW);

        inPotTempBottom = tempBrewKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS).add(BigDecimal.valueOf(-1));
        inPotTempMiddle = tempBrewKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        inPotTempTop = tempBrewKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS).add(BigDecimal.valueOf(1));
    }

    private void calculateFlameTemp() {
        if (flameIsOn) {
            flameTemp = tempBurnerKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        } else {
            flameTemp = tempAmbientAirKelvin.subtract(DIFFERENCE_KELVIN_CELSIUS);
        }

    }



}
