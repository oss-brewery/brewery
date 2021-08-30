package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;


public class FlameControlMockAdapter implements FlameControlAdapter{

    BreweryHardwareSimulation breweryHardwareSimulation = null;

    public FlameControlMockAdapter(BreweryHardwareSimulation breweryHardwareSimulation){
        this.breweryHardwareSimulation = breweryHardwareSimulation;
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
