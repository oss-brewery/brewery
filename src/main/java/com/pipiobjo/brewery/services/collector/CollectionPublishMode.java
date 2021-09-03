package com.pipiobjo.brewery.services.collector;

/**
 * Decision state of what should be done with collected measurement values
 */
public enum CollectionPublishMode {

    /**
     * The collected values should be stored
     */
    PUBLISH_TO_PERSISTENCE,

    /**
     * The collected values should be published to the UI
     */
    PUBLISH_TO_UI,

    /**
     * The collected values should be used for further hardware adjustments or internal handling
     */
    PUBLISH_TO_CALCULATION,

    /**
     * Collect values from all temperature sensors
     */
    COLLECT_TEMPERATURE_SENSORS,

    /**
     * Collect values from flame control sensor
     */
    COLLECT_INPUT_FLAME_SENSOR,

    /**
     * The collected values should be published to the output hardware
     */
    PUBLISH_TO_OUTPUT
}
