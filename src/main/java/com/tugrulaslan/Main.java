package com.tugrulaslan;

import com.tugrulaslan.server.DatabaseServer;
import com.tugrulaslan.server.TomcatServer;

public class Main {
    private static final DatabaseServer databaseServer = DatabaseServer.INSTANCE;
    private static final TomcatServer tomcatServer = TomcatServer.INSTANCE;

    public static void main(String[] args) throws Exception {
        databaseServer.startServer();
        tomcatServer.start().getServer().await();
    }
}
