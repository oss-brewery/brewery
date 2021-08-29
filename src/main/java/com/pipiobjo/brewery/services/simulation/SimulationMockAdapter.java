package com.pipiobjo.brewery.services.simulation;

import javax.inject.Inject;
import java.math.BigDecimal;

public class SimulationMockAdapter implements SimulationAdapter {

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    public void  calculate(BigDecimal stepSizeBD){
        breweryHardwareSimulation.calculate(stepSizeBD);
    }
}


