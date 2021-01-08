package com.pipiobjo.brewery.adapters.flamecontrol;

public interface FlameControlAdapter {
    public void turnOff();

    public void turnOn();

    public void flameOneStepHigher();

    public void flameOneStepLower();

    public void isFlameOn();
}
