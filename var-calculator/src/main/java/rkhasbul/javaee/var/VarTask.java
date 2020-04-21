package rkhasbul.javaee.var;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * VaR calculation task
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
public class VarTask implements Callable<Integer> {

	private static final Logger logger = Logger.getLogger(VarTask.class.getCanonicalName());
	
	private final String ticker;
	
	public VarTask(final String ticker) {
		this.ticker = ticker;
	}

	@Override
    public Integer call() {
        try {
            logger.info(String.format("Starting VaR Calculation Task for tiker '%s'...", ticker));
            Thread.sleep(5000);
            logger.info(String.format("VaR Calculation Task or ticker '%s' has been completed", ticker));
            return ThreadLocalRandom.current().nextInt();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return null;
        }
    }

}
