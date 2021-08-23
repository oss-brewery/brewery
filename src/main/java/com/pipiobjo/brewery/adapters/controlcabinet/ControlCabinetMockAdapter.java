package com.pipiobjo.brewery.adapters.controlcabinet;

import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
import com.pipiobjo.brewery.services.simulation.Pt1Sys;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ControlCabinetMockAdapter implements ControlCabinetAdapter{

    private Pt1Sys pt1SysControlCabinetAirTemp;
    private Pt1Sys pt1SysAirTemp;
    private SensorCollectorServiceConfigProperties configMock;
    private BigDecimal airTemp;
    private BigDecimal controlCabinetAirTemp;
    private int cycleCount;
    private int cycleCountModulo;
    private int maxRandom;
    private int minRandom;
    private int randomNumber;

    public ControlCabinetMockAdapter(SensorCollectorServiceConfigProperties configMock) {
        this.configMock = configMock;
        this.airTemp = BigDecimal.ZERO;
        this.controlCabinetAirTemp = BigDecimal.ZERO;
        this.cycleCount = 0;
        this.cycleCountModulo = 30;
        this.maxRandom = 100;
        this.minRandom = 0;
        this.randomNumber = 0;

        BigDecimal timeConstantControlCabinetAirTemp = BigDecimal.valueOf(20);
        BigDecimal kGainControlCabinetAirTemp = BigDecimal.valueOf(1);
        BigDecimal initialConditionControlCabinetAirTemp = BigDecimal.valueOf(0);
        this.pt1SysControlCabinetAirTemp = new Pt1Sys(
                timeConstantControlCabinetAirTemp, kGainControlCabinetAirTemp, initialConditionControlCabinetAirTemp);

        BigDecimal timeConstantAirTemp = BigDecimal.valueOf(15);
        BigDecimal kGainAirTemp = BigDecimal.valueOf(1);
        BigDecimal initialConditionAirTemp = BigDecimal.valueOf(0);
        this.pt1SysAirTemp = new Pt1Sys(
                timeConstantAirTemp, kGainAirTemp, initialConditionAirTemp);
    }

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public ControlCabinetTemperature getTemparatures() {
        ControlCabinetTemperature result = new ControlCabinetTemperature();
        result.setTimestamp(OffsetDateTime.now());

        // simulation model
        pt1SysAirTemp.calculate(BigDecimal.valueOf(configMock.getTemperatureCollectionIntervallInMS()), airTemp);
        pt1SysControlCabinetAirTemp.calculate(BigDecimal.valueOf(configMock.getTemperatureCollectionIntervallInMS()), controlCabinetAirTemp);

        result.setAirTemp(Optional.of(pt1SysAirTemp.getX()));
        result.setControlCabinetAirTemp(Optional.of(pt1SysControlCabinetAirTemp.getX()));

        if (cycleCount % cycleCountModulo == 0){
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            airTemp = BigDecimal.valueOf(randomNumber);
            randomNumber = ThreadLocalRandom.current().nextInt(minRandom, maxRandom + 1);
            controlCabinetAirTemp = BigDecimal.valueOf(randomNumber);
        }
        cycleCount++;
        return result;
    }
}
