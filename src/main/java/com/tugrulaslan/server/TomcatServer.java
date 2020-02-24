package com.tugrulaslan.server;

import com.tugrulaslan.servlet.AccountsServlet;
import com.tugrulaslan.servlet.ErrorHandlerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import java.io.File;

public enum TomcatServer {
    INSTANCE;
    private static final int PORT = 8080;
    private static final String CONTEXT_PATH = "";
    private static final String PATHNAME = ".";
    private static final Tomcat tomcat = new Tomcat();

    public Tomcat start() throws LifecycleException {
        tomcat.setPort(PORT);
        tomcat.getConnector();

        Context context = tomcat.addContext(CONTEXT_PATH, new File(PATHNAME).getAbsolutePath());

        addAccountServlet(context);
        addErrorServlet(context);

        tomcat.start();
        return tomcat;
    }

    public void stop() throws LifecycleException {
        if (tomcat.getServer() != null
                && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
            if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
                tomcat.stop();
            }
            tomcat.destroy();
        }
    }

    private void addErrorServlet(Context context) {
        Tomcat.addServlet(context, "customErrorServlet", new ErrorHandlerServlet());
        context.addServletMappingDecoded("/error", "customErrorServlet");
        ErrorPage errorPage = new ErrorPage();
        errorPage.setLocation("/error");
        context.addErrorPage(errorPage);
    }

    private void addAccountServlet(Context context) {
        Tomcat.addServlet(context, "AccountsServlet", new AccountsServlet());
        context.addServletMappingDecoded("/accounts/*", "AccountsServlet");
    }
}
