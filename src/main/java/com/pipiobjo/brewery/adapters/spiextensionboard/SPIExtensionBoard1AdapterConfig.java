package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

import javax.inject.Inject;

@ConfigProperties(prefix = "brewery.spi.extensionboard1")
@Data
public class SPIExtensionBoard1AdapterConfig {

    private int controllerId = 0;
    //chiselect == cs // ss -> 0 / 1
    private int chipselect = 0;
    private int freq = 12_500_000 - 1000;  // why -1000?

    // TODO public or private?
    public static final PortPin led1 = new PortPin(1,'B',0);
    public static final PortPin gfa230VRelais = new PortPin(1,'B',1);
    public static final PortPin motor1Pul = new PortPin(1,'B',3);
    public static final PortPin motor1Dir = new PortPin(1,'B',5);
    public static final PortPin motor1En = new PortPin(1,'B',7);
    public static final PortPin beep = new PortPin(2,'B',1);

}


