package com.pipiobjo.brewery.adapters.monotorcontrol;

public class MotorControlMockAdapter implements MotorControlAdapter{
    @Override
    public long getMaxPosition() {
        return 0;
    }

    @Override
    public long getMinPosition() {
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public boolean moveToPosition(long targetPosition) {
        return false;
    }
}
