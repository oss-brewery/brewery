package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;

import javax.inject.Inject;

public class FlameControlMockAdapter implements FlameControlAdapter{

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    public FlameControlMockAdapter() {
    }

    @Override
    public void turnOff() {
        breweryHardwareSimulation.setFlameIsOn(false);
    }

    @Override
    public void turnOn() {
        breweryHardwareSimulation.setFlameIsOn(true);
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
        return breweryHardwareSimulation.isFlameIsOn();
    }
}
