package com.nexus;

import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

/**
 * A reusable Testcontainer for PostgreSQL.
 * This ensures that the same container is shared across all tests,
 * with random database names to support parallel execution.
 */
public class TestPostgresContainer extends PostgreSQLContainer<TestPostgresContainer> {

    private static TestPostgresContainer container;

    private TestPostgresContainer() {
        super("postgres:latest");
        // Configure container settings
        this.withDatabaseName("testdb_" + UUID.randomUUID()) // Random database name for isolation
                .withUsername("testuser")
                .withPassword("testpassword");
    }

    /**
     * Provides a singleton instance of the TestPostgresContainer.
     * The container is started only once and reused across all tests.
     *
     * @return the singleton instance of TestPostgresContainer
     */
    public static TestPostgresContainer getInstance() {
        if (container == null) {
            container = new TestPostgresContainer();
            container.start();
        }
        return container;
    }

    @Override
    public void stop() {
        // Do nothing to prevent the container from being stopped.
        // This allows the container to be reused across multiple tests.
    }
}
