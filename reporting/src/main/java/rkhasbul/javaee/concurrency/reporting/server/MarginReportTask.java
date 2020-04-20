package rkhasbul.javaee.concurrency.reporting.server;

import java.util.logging.Logger;

/**
 * Margin Report generation task
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
public class MarginReportTask implements Runnable {
	
	private static final Logger logger = Logger.getLogger(MarginReportTask.class.getCanonicalName());
	
	@Override
    public void run() {
        try {
            logger.info("Starting Margin Report Task...");
            Thread.sleep(10000);
            logger.info("Margin Report Task has been finished successfully");
        } catch (InterruptedException ex) {
            logger.severe(ex.getMessage());
        }
    }

}
