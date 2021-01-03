package com.pipiobjo.brewery.services;

import com.pipiobjo.brewery.services.collector.CollectionPublishMode;
import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.collector.SensorCollectorServiceConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SensorCollectorServiceTest {


    @Test
    void selectCollectionMode_0_Iteration() throws Exception {
        SensorCollectorService service = new SensorCollectorService();
        assertThat(service).isNotNull();

        StopWatch stopWatch = new StopWatch();
        Long it = 0L;
        SensorCollectorServiceConfigProperties config = new SensorCollectorServiceConfigProperties();
        config.setBaseCollectionIntervallInMS(1000);

        stopWatch.start();
        List<CollectionPublishMode> modes = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();
        long time = stopWatch.getTime();
        log.info("Method execution time {} ms", time);
        assertThat(modes)
                .isNotNull()
                .isEmpty();

    }

    @Test
    void selectCollectionMode_Iteration() throws Exception {
        SensorCollectorService service = new SensorCollectorService();
        assertThat(service).isNotNull();

        StopWatch stopWatch = new StopWatch();
        SensorCollectorServiceConfigProperties config = new SensorCollectorServiceConfigProperties();
        config.setBaseCollectionIntervallInMS(10);
        config.setInputCollectionIntervallInMS(33);
        config.setTemperatureCollectionIntervallInMS(100);
        config.setCalculationIntervallInMS(150);
        config.setPersistenceIntervallInMS(2000);
        config.setUiUpdateIntervallInMS(2000);

        Long it = 1L;
        stopWatch.start();
        List<CollectionPublishMode> modes1 = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();

        log.info("Method execution time {} ms", stopWatch.getTime());
        stopWatch.reset();

        assertThat(modes1)
                .isNotNull()
                .isEmpty();


        it = 6L;
        stopWatch.start();
        List<CollectionPublishMode> modes2 = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();

        log.info("Method execution time {} ms", stopWatch.getTime());
        stopWatch.reset();

        assertThat(modes2)
                .isNotNull()
                .hasSize(1)
                .contains(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);


        it = 10L;
        stopWatch.start();
        List<CollectionPublishMode> modes3 = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();

        log.info("Method execution time {} nanos", stopWatch.getNanoTime());
        stopWatch.reset();

        assertThat(modes3)
                .isNotNull()
                .hasSize(1)
                .contains(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS);


        it = 30L;
        stopWatch.start();
        List<CollectionPublishMode> modes4 = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();

        log.info("Method execution time {} nanos", stopWatch.getNanoTime());
        stopWatch.reset();

        assertThat(modes4).isNotNull()
                .hasSize(2)
                .contains(CollectionPublishMode.COLLECT_TEMPERATURE_SENSORS)
                .contains(CollectionPublishMode.PUBLISH_TO_CALCULATION);


    }
}