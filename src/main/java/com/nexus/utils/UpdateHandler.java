package com.nexus.utils;

import com.nexus.abstraction.AbstractAppAuditing;
import com.nexus.exception.NoUpdateException;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdateHandler {

    /**
     * Updates fields of an entity and saves it if any changes were made.
     *
     * @param updateLogic  A Consumer that defines the fields to update.
     * @param saveAction   The action to save the updated entity.
     */
    public static void updateEntity( Consumer<ChangeTracker> updateLogic, Runnable saveAction) {
        ChangeTracker tracker = new ChangeTracker();
        updateLogic.accept(tracker);

        if (tracker.hasNotChanged()) {
            throw new NoUpdateException("No update has been made");
        }

        saveAction.run();
    }

    /**
     * Updates fields of an entity, saves it if any changes were made, and monitors the changes.
     *
     * @param entity         The entity to update.
     * @param updateLogic    A Consumer that defines the fields to update.
     * @param saveAction     The action to save the updated entity.
     * @param monitorManager The MonitorManager instance to handle monitoring.
     * @param <T>            The type of the entity.
     */
    public static <T extends AbstractAppAuditing<?>> void updateEntity(
            T entity,
            Consumer<ChangeTracker> updateLogic,
            Runnable saveAction,
            MonitorManager monitorManager
    ) {
        ChangeTracker tracker = new ChangeTracker();
        updateLogic.accept(tracker);

        if (tracker.hasNotChanged()) {
            throw new NoUpdateException("No update has been made");
        }

        saveAction.run();

        String context = tracker.getContext();

        monitorManager.monitor(entity, ActionType.UPDATE, context);
    }

    /**
     * Encapsulates the logic for tracking field changes.
     */
    public static class ChangeTracker {
        private boolean changed = false;
        private final StringBuilder contextBuilder = new StringBuilder();

        public <V> void updateField(Supplier<V> currentValue, V newValue, Consumer<V> updateAction) {
            if (newValue != null && !Objects.equals(currentValue.get(), newValue)) {
                V oldValue = currentValue.get();
                updateAction.accept(newValue);
                this.changed = true;
                contextBuilder.append(String.format("Field updated: old value = %s, new value = %s\n", oldValue, newValue));
            }
        }

        public boolean hasNotChanged() {
            return !changed;
        }

        public String getContext() {
            return contextBuilder.toString();
        }
    }
}
