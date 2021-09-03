package com.pipiobjo.brewery.adapters.io;


import com.pipiobjo.brewery.services.controller.GeneralControllerCycle;
import com.pipiobjo.brewery.services.model.OutputResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ReadOutputState {

    @Inject
    GeneralControllerCycle generalControllerCycle;

    @PostConstruct
    void init() {
        log.info("Output collector is starting");
    }

    public OutputResult getOutputs(){
        OutputResult outputResult = new OutputResult();

        outputResult.setMotorControlTargetPercent(generalControllerCycle.getMotorMovePercent());

        return outputResult;
    }
}
