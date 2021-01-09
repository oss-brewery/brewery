package com.pipiobjo.brewery.persistence;

import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.CollectionResult;
import io.quarkus.vertx.ConsumeEvent;
import lombok.extern.slf4j.Slf4j;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class SensorDataPersistenceService {

    private SensorDataPersistenceConfigProperties config;

    public SensorDataPersistenceService(SensorDataPersistenceConfigProperties config){
        this.config = config;

        MVStore mvStore = new MVStore.Builder().fileName("filename").open();
        MVMap<Object, Object> sensorRecording = mvStore.openMap("sensorRecording");
        sensorRecording.put("", "");

        mvStore.close();
    }

    @ConsumeEvent(value = SensorCollectorService.PUBLISH_TO_PERSISTENCE_EVENT_NAME, blocking = true)
    public void updateUIEvent(CollectionResult event) {
        log.info("receiving collection result: {}", event);
    }
}
