package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

import javax.inject.Inject;
import java.util.Map;

@ConfigProperties(prefix = "brewery.spi.extensionboard1")
@Data
public class SPIExtensionBoardAdapterConfigProperties {

    private int controllerId = 0;
    private int chipselect = 0;             // chip select == cs // ss -> 0 / 1
    private int freq = 12_500_000 - 1000;   // why -1000?

    Map<String, PortPin> portpinmap = Map.of(
            "led1", new PortPin(1,'B',0),
            "gfa230VRelais", new PortPin(1,'B',3),
            "motor1Pul", new PortPin(1,'B',5),
            "motor1Dir", new PortPin(1,'B',5),
            "motor1En", new PortPin(1,'B',7),
            "beep", new PortPin(2,'B',1)
    );

}


