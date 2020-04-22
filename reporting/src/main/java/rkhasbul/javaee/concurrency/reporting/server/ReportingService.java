package rkhasbul.javaee.concurrency.reporting.server;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import rkhasbul.javaee.concurrency.reporting.client.ReportType;

/**
 * Report Generation Service 
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
@Stateless
@Path("/report")
public class ReportingService {
	
	private static final Logger logger = Logger.getLogger(ReportingService.class.getCanonicalName());

    @Resource(lookup = "java:jboss/ee/concurrency/scheduler/reporting")
    private ManagedScheduledExecutorService reportingExecutor;
    
    @EJB
    private ReportingTasks reportingTasks;

    @POST
    @Path("/schedule")
    public Response schedule(final @QueryParam("reportType") ReportType reportType) {
    	logger.info(String.format("Scheduling %s report task...", reportType.getLabel()));
        try {
        	final Runnable task = reportingTasks.getTask(reportType);
        	final ScheduledFuture<?> scheduledTask = reportingExecutor.schedule(
        			task, 30, TimeUnit.SECONDS);
        	reportingTasks.put(reportType, scheduledTask);
        } catch (RejectedExecutionException ree) {
            return Response
            		.status(SERVICE_UNAVAILABLE)
            		.entity(String.format("%s report task not been scheduled. %s", reportType.getLabel(), ree.getMessage()))
            		.build();
        }

        return Response
        		.status(CREATED)
        		.entity(String.format("%s report task successfully scheduled.", reportType.getLabel()))
        		.build();
    }

    @GET
    @Path("/tasks")
    public Response tasks() {
    	logger.info("Providing list of scheduled tasks...");
		return Response
				.status(OK)
				.entity(reportingTasks.getTasks())
				.build();
    } 
    
    @DELETE
    @Path("/cancel/{scheduledTask}")
    public Response cancel(final @PathParam("scheduledTask") String scheduledTask) {
    	logger.info(String.format("Cancelling '%s' scheduled task...", scheduledTask));
    	reportingTasks.deleteTask(scheduledTask);
    	return Response
    			.status(OK)
    			.build();
    }
    
}
