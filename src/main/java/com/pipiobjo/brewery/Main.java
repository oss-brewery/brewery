package com.pipiobjo.brewery;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@QuarkusMain
public class Main {
    /**
     * Running main from intellij don't works yet, see open issue https://github.com/quarkusio/quarkus/issues/8737
     */
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {

        @Override
        public int run(String... args) throws Exception {
            log.info("Do startup logic here");

            for (String arg : args) {
                log.info("arg={}", arg);
            }
            Quarkus.waitForExit();
            return 0;
        }
    }
}