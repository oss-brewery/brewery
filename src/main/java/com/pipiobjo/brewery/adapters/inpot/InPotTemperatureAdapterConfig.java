package com.pipiobjo.brewery.adapters.inpot;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Dependent
public class InPotTemperatureAdapterConfig {

//    @Produces
//    @IfBuildProfile("mockDevices")
//    public InPotTemperatureAdapter realTracer() {
//        return new RealTracer(reporter, configuration);
//    }
//
//
//    @Produces
//    @DefaultBean
//    public InPotTemperatureAdapter noopTracer(InPotTemperatureConfigProperties config) {
//        return new InPotTemperatureDeviceAdapter(config);
//    }
}
