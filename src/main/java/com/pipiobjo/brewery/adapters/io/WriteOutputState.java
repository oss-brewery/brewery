package com.pipiobjo.brewery.adapters.io;

import com.pipiobjo.brewery.adapters.monotorcontrol.MotorControlAdapter;
import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.OutputResult;
import io.quarkus.vertx.ConsumeEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Data
@Slf4j
@ApplicationScoped
public class WriteOutputState {

    @Inject
    SPIExtensionBoardAdapter extensionBoard;
    @Inject
    MotorControlAdapter motorControlAdapter;

    @PostConstruct
    void init() {
        log.info("Output adapter is starting");
    }

    @ConsumeEvent(value = SensorCollectorService.PUBLISH_TO_OUTPUT_EVENT_NAME, blocking = true)
    public void writeToOutputs(OutputResult event){
        motorControlAdapter.moveToPercent(event.getMotorControlTargetPercent());
//        return true;  // TODO disussion if something like acknowledge is needed or how error handling shut be.
    }

}
