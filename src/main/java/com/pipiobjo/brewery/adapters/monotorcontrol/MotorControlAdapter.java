package com.pipiobjo.brewery.adapters.monotorcontrol;

public interface MotorControlAdapter {


    public long getMaxPosition();
    public long getMinPosition();
    public long getCurrentPosition();
    
    public boolean moveToPosition(long targetPosition);
}
