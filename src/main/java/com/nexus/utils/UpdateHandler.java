package com.nexus.utils;

import com.nexus.exception.NoUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        if (!tracker.hasChanges()) {
            throw new NoUpdateException("No update has been made");
        }

        saveAction.run();
    }

    /**
     * Encapsulates the logic for tracking field changes.
     */
    public static class ChangeTracker {
        private boolean changed = false;
        private static final Logger LOGGER = LoggerFactory.getLogger(ChangeTracker.class);

        public <V> void updateField(Supplier<V> currentValue, V newValue, Consumer<V> updateAction) {
            if (newValue != null && !Objects.equals(currentValue.get(), newValue)) {
                updateAction.accept(newValue);
                this.changed = true;

                LOGGER.debug("old value of: {} changed to: {}", currentValue.get(), newValue);
            }
        }

        public boolean hasChanges() {
            return changed;
        }
    }
}
