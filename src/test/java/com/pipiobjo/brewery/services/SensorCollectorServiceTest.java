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
        assertThat(modes).isNotNull();
        assertThat(modes).hasSize(0);

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

        Long it = 1L;
        stopWatch.start();
        List<CollectionPublishMode> modes1 = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();

        log.info("Method execution time {} ms", stopWatch.getTime());
        stopWatch.reset();

        assertThat(modes1).isNotNull();
        assertThat(modes1).hasSize(0);


        it = 6L; //3L
        stopWatch.start();
        List<CollectionPublishMode> modes2 = Whitebox.invokeMethod(service, "selectCollectionMode", it, config);
        stopWatch.stop();

        log.info("Method execution time {} ms", stopWatch.getTime());
        stopWatch.reset();
        assertThat(modes2).isNotNull();
        assertThat(modes2).hasSize(1);
        CollectionPublishMode collectionPublishMode = modes2.get(0);

        assertThat(collectionPublishMode).isNotNull();
        assertThat(collectionPublishMode).isEqualTo(CollectionPublishMode.COLLECT_INPUT_FLAME_SENSOR);


    }
}