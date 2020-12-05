package com.pipiobjo.brewery.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DeviceManager {
    private Logger log = LoggerFactory.getLogger(DeviceManager.class);


    public int getStatusLedPort(){
        return 24;
    }

    public int getBreweryStartButtonPort() {
        return 23;
    }
}
