package com.pipiobjo.brewery.adapters.flamecontrol;

import java.math.BigDecimal;

public interface FlameControlAdapter {
    public void turnOff();

    public void turnOn();

    public void increaseFlameByOneStep();

    public void decreaseFlameByOneStep();

    public void setPointTempCelsius(BigDecimal setpoint);

    public boolean isFlameOn();
}
