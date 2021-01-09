package com.pipiobjo.brewery.adapters.spiextensionboard;

import lombok.Data;

@Data
public class PortPin {
    private int mcpNumber;
    private char port;
    private int pin;

    public PortPin(int mcpNumber, char port, int pin) {
        this.mcpNumber = mcpNumber;
        this.port = port;
        this.pin = pin;
    }

    public int getMcpNumber(){
        return mcpNumber;
    }
    public char getPort(){
        return port;
    }
    public int getPin(){
        return pin;
    }
}
