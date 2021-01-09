package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

import java.util.Optional;

@Data
@ConfigProperties(failOnMismatchingMember = false)
public class PortPinConfigProperties {
    public Optional<Integer> mcpNumber;
    public Optional<Character> port;
    public Optional<Integer> pin;
}
