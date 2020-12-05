package com.pipiobjo.brewery;

import com.pipiobjo.brewery.services.BrewingService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class Main {
    /**
     * Running main from intellij don't works yet, see open issue https://github.com/quarkusio/quarkus/issues/8737
     */
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {
        @Inject
        BrewingService brewingService;

        @Override
        public int run(String... args) throws Exception {
            System.out.println("Do startup logic here" );
            brewingService.init();
            for (String arg: args) {
                System.out.println("arg=" + arg);
            }
            Quarkus.waitForExit();
            return 0;
        }
    }
}