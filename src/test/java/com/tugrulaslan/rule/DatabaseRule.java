package com.tugrulaslan.rule;

import com.tugrulaslan.server.DatabaseServer;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DatabaseRule extends TestWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseRule.class);
    private final DatabaseServer databaseServer = DatabaseServer.INSTANCE;

    @Override
    protected void starting(Description description) {
        try {
            databaseServer.startServer();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error occurred while starting the database server ", e);
        }
    }

    @Override
    protected void finished(Description description) {
        databaseServer.stopServer();
    }
}
