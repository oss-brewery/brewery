package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;

public class FlameControlMockAdapter implements FlameControlAdapter{
    public FlameControlMockAdapter() {
    }

    @Override
    public void turnOff() {
        BreweryHardwareSimulation.setFlameIsOn(false);
    }

    @Override
    public void turnOn() {
        BreweryHardwareSimulation.setFlameIsOn(true);
    }

    @Override
    public void increaseFlameByOneStep() {
        // TODO implement HardwareSimulation
    }

    @Override
    public void decreaseFlameByOneStep() {
        // TODO implement HardwareSimulation
    }

    @Override
    public boolean isFlameOn() {
        return BreweryHardwareSimulation.isFlameIsOn();
    }
}
