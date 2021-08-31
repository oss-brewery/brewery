package com.pipiobjo.brewery.adapters.alarm;

import io.quarkus.arc.profile.IfBuildProfile;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class AlarmMockAdapter implements AlarmAdapter{
    @Override
    public void turnAlarmOn() {

    }

    @Override
    public void turnAlarmOff() {

    }

    @Override
    public void turnAlarmOnForMilliseconds(long milliseconds) {

    }
}
