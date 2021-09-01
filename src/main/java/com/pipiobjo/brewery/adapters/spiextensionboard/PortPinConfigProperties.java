package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigProperties;

import java.util.Optional;

@ConfigProperties(failOnMismatchingMember = false)
public class PortPinConfigProperties {
    public Optional<Integer> mcpNumber;
    public Optional<Character> port;
    public Optional<Integer> pin;
}
