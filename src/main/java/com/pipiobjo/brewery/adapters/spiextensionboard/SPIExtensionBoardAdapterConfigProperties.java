package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigPrefix;
import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@ApplicationScoped
//@ConfigProperties(prefix = "brewery.spi.extensionboard")
public class SPIExtensionBoardAdapterConfigProperties {

    public static enum GPIOMCP {
        LED1, BEEP, GFA_230V_RELAIS,
        MOTOR1_PUL, MOTOR1_DIR, MOTOR1_EN,
        MOTOR2_PUL, MOTOR2_DIR, MOTOR2_EN,
        ;
    }


    int controllerId = 0;
    int chipselect = 0;             // chip select == cs // ss -> 0 / 1
    int freq = 12_500_000 - 1000;   // why -1000?

    @ConfigPrefix("brewery.spi.extensionboard.led1")
    PortPinConfigProperties led1;

    @ConfigPrefix("brewery.spi.extensionboard.beep")
    PortPinConfigProperties beep;

    @ConfigPrefix("brewery.spi.extensionboard.gfa230-v-relais")
    PortPinConfigProperties gfa230VRelais;

    @ConfigPrefix("brewery.spi.extensionboard.motor1-pul")
    PortPinConfigProperties motor1Pul;

    @ConfigPrefix("brewery.spi.extensionboard.motor1-dir")
    PortPinConfigProperties motor1Dir;

    @ConfigPrefix("brewery.spi.extensionboard.motor1-en")
    PortPinConfigProperties motor1En;

    @ConfigPrefix("brewery.spi.extensionboard.motor2-dir")
    PortPinConfigProperties motor2Dir;

    @ConfigPrefix("brewery.spi.extensionboard.motor2-en")
    PortPinConfigProperties motor2En;

    @ConfigPrefix("brewery.spi.extensionboard.motor2-pul")
    PortPinConfigProperties motor2Pul;


    public Map<GPIOMCP, PortPinConfigProperties>getGPIOMCPMap(){
        Map<GPIOMCP, PortPinConfigProperties> result = new HashMap<>();
        result.put(GPIOMCP.LED1,led1);
        result.put(GPIOMCP.BEEP,beep);
        result.put(GPIOMCP.GFA_230V_RELAIS,gfa230VRelais);
        result.put(GPIOMCP.MOTOR1_PUL,motor1Pul);
        result.put(GPIOMCP.MOTOR1_DIR,motor1Dir);
        result.put(GPIOMCP.MOTOR1_EN,motor1En);

        result.put(GPIOMCP.MOTOR2_PUL,motor2Pul);
        result.put(GPIOMCP.MOTOR2_DIR,motor2Dir);
        result.put(GPIOMCP.MOTOR2_EN,motor2En);


        return result;

    }

}

