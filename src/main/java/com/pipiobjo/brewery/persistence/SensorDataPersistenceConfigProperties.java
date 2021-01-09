package com.pipiobjo.brewery.persistence;

import io.quarkus.arc.config.ConfigProperties;

import java.io.File;

@ConfigProperties(prefix = "brewery.sensor.datarecording")
public class SensorDataPersistenceConfigProperties {
    File storageDirectory;
}
