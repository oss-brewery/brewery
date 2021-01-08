package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@ConfigProperties(prefix = "brewery.spi.extensionboard1")
@Data
public class SPIExtensionBoard1AdapterConfig {
    private int controllerId = 0;
    //chiselect == cs // ss -> 0 / 1
    private int chipselect = 0;
    private int freq = 12_500_000 - 1000;  // why -1000?

    private int LEDPort = 0;
    private boolean activatePort1 = true;
    private boolean activatePort2 = true;
    private boolean activatePort3 = true;
    private boolean activatePort4 = true;
    private boolean activatePort5 = true;
    private boolean activatePort6 = true;
    private boolean activatePort7 = true;


}


