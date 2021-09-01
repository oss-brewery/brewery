package com.pipiobjo.brewery.adapters.monotorcontrol;

import java.math.BigDecimal;

public interface MotorControlAdapter {


    public long getMaxPosition();
    public long getMinPosition();
    public long getCurrentPosition();
    
    public boolean moveToPosition(long targetPosition);

    /**
     * Moving to a target position by defining a percentage
     * @param targetPercent is the percentage to move [0-100].
     * @return if it was correct.
     */
    default boolean moveToPercent(BigDecimal targetPercent){
        return moveToPosition( (long) ((getMaxPosition()-getMinPosition()) * targetPercent.doubleValue() / 100) );
    }
}
