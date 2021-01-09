package com.pipiobjo.brewery.rest;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BreweryBasicITestProfile implements QuarkusTestProfile {

    /**
     * Returns additional config to be applied to the test. This
     * will override any existing config (including in application.properties),
     * however existing config will be merged with this (i.e. application.properties
     * config will still take effect, unless a specific config key has been overridden).
     */
    public Map<String, String> getConfigOverrides() {
        Map<String, String> props = new HashMap<>();
        props.put("quarkus.http.test-port", "0"); // random http port

        return props;
    }

    /**
     * Returns enabled alternatives.
     *
     * This has the same effect as setting the 'quarkus.arc.selected-alternatives' config key,
     * however it may be more convenient.
     */
    public Set<Class<?>> getEnabledAlternatives() {
        return Collections.emptySet();
    }

    /**
     * Allows the default config profile to be overridden. This basically just sets the quarkus.test.profile system
     * property before the test is run.
     *
     */
    public String getConfigProfile() {
        return "mockDevices";
    }
}
