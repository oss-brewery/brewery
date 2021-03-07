package com.pipiobjo.brewery.adapters.spiextensionboard;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapterConfigProperties;
import com.pipiobjo.brewery.rest.BreweryBasicITestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
@TestProfile(BreweryBasicITestProfile.class)
@QuarkusTest
public class ConfigITest {
    @Inject
    SPIExtensionBoardAdapterConfigProperties config;

    @Test
    public void testConfigValues() throws Exception {
        assertThat(config).isNotNull();
        PortPinConfigProperties led1 = config.getLed1();
        assertThat(led1).isNotNull();

        Optional<Integer> led1McpNumber = led1.mcpNumber;
        assertThat(led1McpNumber).isNotEmpty();
        Integer led1McpNumberValue = led1McpNumber.get();
        assertThat(led1McpNumberValue).isEqualTo(1);
    }
}
