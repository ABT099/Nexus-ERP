package com.nexus.monitor;

import com.nexus.abstraction.AbstractAppAuditing;
import com.nexus.ineteraction.InterceptionRepository;
import com.nexus.notification.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class MonitorManager {
    private final static Logger LOG = LoggerFactory.getLogger(MonitorManager.class);
    private static final Map<Class<?>, MonitoringStrategy<?>> strategies = new HashMap<>();

    private final Executor taskExecutor;
    private final InterceptionRepository interceptionRepository;
    private final NotificationManager notificationManager;

    public MonitorManager(
            @Qualifier("taskExecutor") Executor taskExecutor,
            InterceptionRepository interceptionRepository,
            NotificationManager notificationManager
    ) {
        this.taskExecutor = taskExecutor;
        this.interceptionRepository = interceptionRepository;
        this.notificationManager = notificationManager;
    }

    public <T extends AbstractAppAuditing<?>> void monitor(T entity, String... args) {

        if (entity == null) {
            throw new IllegalStateException("No entity found");
        }

        CompletableFuture.runAsync(() -> {
            try {
                LOG.debug("[Async Monitoring] Thread: {}", Thread.currentThread());

                Class<?> entityClass = entity.getClass();
                LOG.debug("Monitoring entity of type: {}", entityClass.getSimpleName());

                MonitoringStrategy<T> strategy = (MonitoringStrategy<T>) strategies.get(entityClass);

                if (strategy == null) {
                    throw new IllegalStateException("No strategy found for " + entityClass.getSimpleName());
                }

                strategy.handle(entity, interceptionRepository, notificationManager, args);
            } catch (Exception e) {
                LOG.error("[Async Monitoring] Thread: {}", Thread.currentThread(), e);
            }
        }, taskExecutor);
    }

    public static <S extends AbstractAppAuditing<?>> void registerStrategy(
            Class<S> clazz,
            MonitoringStrategy<S> strategy) {
        strategies.put(clazz, strategy);
        LOG.info("Registered monitoring strategy for {}", clazz.getSimpleName());
    }

    @FunctionalInterface
    public interface MonitoringStrategy<S extends AbstractAppAuditing<?>> {
        void handle(S entity,
                    InterceptionRepository repository,
                    NotificationManager notification,
                    String... args) throws Exception;
    }
}
