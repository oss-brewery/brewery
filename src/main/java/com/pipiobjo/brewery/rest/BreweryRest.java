package com.pipiobjo.brewery.rest;

import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureConfigProperties;
import com.pipiobjo.brewery.adapters.spiextensionboard.PortPinConfigProperties;
import com.pipiobjo.brewery.rest.model.BrewReceipt;
import com.pipiobjo.brewery.services.BrewingService;
import com.pipiobjo.brewery.services.model.SelfCheckResult;
import io.quarkus.arc.config.ConfigPrefix;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/brewery")
public class BreweryRest {

    @Inject
    BrewingService brewingService;


    @GET
    @Path("/selfcheck")
    @Produces(MediaType.APPLICATION_JSON)
    public SelfCheckResult selfcheck() {

        log.info("start selfcheck");
        SelfCheckResult result = brewingService.runSelfcheck();
        log.info("finished selfcheck: {}", result);

        return result;
    }


    @POST
    @Path("/configuration/receipt")
    @Produces(MediaType.APPLICATION_JSON)
    public String configureReceipt(BrewReceipt receipt) {

        log.info("configure brewery with receipt {}", receipt);

        return "hello2";
    }



    @POST
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public void start() {

        brewingService.startCollecting();
    }



    @POST
    @Path("/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public void stopBrewing() {

        brewingService.stopCollecting();
    }

    @Inject
    InPotTemperatureConfigProperties inPotTemperatureConfigProperties;

    @POST
    @Path("/testFun")
    public void startTestFun(){
    log.info("a function for testing");
    log.info(inPotTemperatureConfigProperties.getW1TempBottomID());
    log.info("end of testing");

    }

}