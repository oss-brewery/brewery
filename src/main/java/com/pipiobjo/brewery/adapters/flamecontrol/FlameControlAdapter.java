package com.pipiobjo.brewery.adapters.flamecontrol;

public interface FlameControlAdapter {
    public void turnOff();

    public void turnOn();

    public void increaseFlameByOneStep();

    public void decreaseFlameByOneStep();

    public boolean isFlameOn();
}
