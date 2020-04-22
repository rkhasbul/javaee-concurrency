package rkhasbul.javaee.var;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * Report Generation Service 
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
@Stateless
@Path("/{ticker}")
public class VarCalculator {

	private static final Logger logger = Logger.getLogger(VarCalculator.class.getCanonicalName());

    @Resource(lookup = "java:jboss/ee/concurrency/executor/var")
    private ManagedExecutorService varExecutor;

    @EJB
    private VarStorage varStorage;
    
    @GET
    @Path("/calculate")
    public Response calculate(final @PathParam("ticker") String ticker) {
    	logger.info("Calculating VaR for '%s' ticker...");
        try {
            Future<Integer> submit = varExecutor.submit(new VarTask(ticker));
            varStorage.put(ticker, submit);
        } catch (RejectedExecutionException ree) {
            return Response.status(SERVICE_UNAVAILABLE)
            		.entity(String.format("VaR calculation task for ticker '%s' not submitted. %s", ticker, ree.getMessage()))
            		.build();
        }

        return Response.status(OK)
        		.entity(String.format("VaR calculation task for ticker '%s' successfully submitted.", ticker))
        		.build();
    }
	
    @GET
    public Response getResult(final @PathParam("ticker") String ticker) {
    	logger.info(String.format("Requesting VaR result for '%s' ticker...", ticker));
    	final Future<Integer> future = varStorage.get(ticker);
    	ResponseBuilder responseBuilder = null;
    	if (future != null) {
    		try {
    			Integer result = future.get();
    			logger.info(String.format("VaR result: %d", result));
    			responseBuilder = Response.status(OK).entity(result);
    		} catch (Exception e) {
    			responseBuilder = Response.status(SERVICE_UNAVAILABLE).entity(e.getMessage());
    		}
    	} else {
    		responseBuilder = Response.status(NOT_FOUND);
    	}
		return responseBuilder.build();
    }
    
}
