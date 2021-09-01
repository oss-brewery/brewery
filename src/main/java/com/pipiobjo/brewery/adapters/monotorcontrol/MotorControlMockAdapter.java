package com.pipiobjo.brewery.adapters.monotorcontrol;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.arc.profile.IfBuildProfile;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class MotorControlMockAdapter implements MotorControlAdapter{

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @Override
    public long getMaxPosition() {
        return breweryHardwareSimulation.MAX_INCREMENTS_MOTOR.longValue();
    }

    @Override
    public long getMinPosition() {
        return breweryHardwareSimulation.MIN_INCREMENTS_MOTOR.longValue();
    }

    @Override
    public long getCurrentPosition() {
        return breweryHardwareSimulation.getIncrementsMotor().longValue();
    }

    @Override
    public boolean moveToPosition(long targetPosition) {
        if (targetPosition < getMaxPosition() || targetPosition > getMinPosition()){
            breweryHardwareSimulation.setIncrementsMotor(BigDecimal.valueOf(targetPosition));
            return true;
        }else {
            return false;
        }
    }
}
