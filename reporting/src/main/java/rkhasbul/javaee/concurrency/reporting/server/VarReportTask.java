package rkhasbul.javaee.concurrency.reporting.server;

import java.util.logging.Logger;

/**
 * VaR Report generation task
 *
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
public class VarReportTask implements Runnable {

    private static final Logger logger = Logger.getLogger(VarReportTask.class.getCanonicalName());

    @Override
    public void run() {
        try {
            logger.info("Starting VaR Report Task...");
            Thread.sleep(5000);
            logger.info("VaR Report Task has been finished successfully");
        } catch (InterruptedException ex) {
            logger.severe(ex.getMessage());
        }
    }

}