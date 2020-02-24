package com.tugrulaslan.server;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public enum DatabaseServer {
    INSTANCE;
    private static final String SCHEMA_SQL = "schema.sql";
    private static final String URL = "jdbc:h2:mem:tcp://127.0.1.1:9094/mydb2";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServer.class);

    private Server tcpServer = null;
    private Connection connection = null;

    public void startServer() throws InterruptedException, IOException {
        new Thread(() -> {
            try {
                tcpServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9094");
                tcpServer.start();
            } catch (SQLException e) {
                LOGGER.error("Error occurred ", e);
            }
        }).start();
        waitUntilServerStart();
        initiateSchema();
    }

    public void stopServer() {
        if (tcpServer != null) {
            tcpServer.stop();
        }
    }

    private void waitUntilServerStart() throws InterruptedException {
        while (tcpServer == null) {
            Thread.sleep(40);
        }
    }

    private void initiateSchema() throws IOException {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            statement.execute(ResourceUtils.read(SCHEMA_SQL));
        } catch (SQLException e) {
            LOGGER.error("Error ", e);
        }
    }
}
