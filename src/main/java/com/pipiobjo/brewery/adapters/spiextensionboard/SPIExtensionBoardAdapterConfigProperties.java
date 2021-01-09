package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@ConfigProperties(prefix = "brewery.spi.extensionboard")
public class SPIExtensionBoardAdapterConfigProperties {

    public static enum GPIOMCP {
        LED1, BEEP, GFA_230V_RELAIS,
        MOTOR1_PUL, MOTOR1_DIR, MOTOR1_EN,
        MOTOR2_PUL, MOTOR2_DIR, MOTOR2_EN,
        ;
    }


    private int controllerId = 0;
    private int chipselect = 0;             // chip select == cs // ss -> 0 / 1
    private int freq = 12_500_000 - 1000;   // why -1000?

    private PortPin led1;
    private PortPin beep;
    private PortPin gfa230VRelais;
    private PortPin motor1Pul;
    private PortPin motor1Dir;
    private PortPin motor1En;
    private Optional<PortPin> motor2En;
    private Optional<PortPin> motor2Dir;
    private Optional<PortPin> motor2Pul;


    public Map<GPIOMCP, PortPin>getGPIOMCPMap(){
        Map<GPIOMCP, PortPin> result = new HashMap<>();
        result.put(GPIOMCP.LED1,led1);
        result.put(GPIOMCP.BEEP,beep);
        result.put(GPIOMCP.GFA_230V_RELAIS,gfa230VRelais);
        result.put(GPIOMCP.MOTOR1_PUL,motor1Pul);
        result.put(GPIOMCP.MOTOR1_DIR,motor1Dir);
        result.put(GPIOMCP.MOTOR1_EN,motor1En);

        if(motor2En.isPresent() && motor2Dir.isPresent() && motor2Pul.isPresent()){
            result.put(GPIOMCP.MOTOR2_PUL,motor2Pul.get());
            result.put(GPIOMCP.MOTOR2_DIR,motor2Dir.get());
            result.put(GPIOMCP.MOTOR2_EN,motor2En.get());
        }


        return result;

    }
}


