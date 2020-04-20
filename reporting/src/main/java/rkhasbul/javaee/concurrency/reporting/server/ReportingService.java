package rkhasbul.javaee.concurrency.reporting.server;

import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("/reporting-service")
public class ReportingService {
	
	private static final Logger logger = Logger.getLogger(ReportingService.class.getCanonicalName());

    @Resource(lookup = "java:jboss/ee/concurrency/executor/reporting")
    private ManagedExecutorService managedExecutorService;

    @EJB
    private ReportTaskFactory reportTaskFactory;

    @POST
    @Path("/process")
    public Response process(final @QueryParam("reportType") ReportType reportType) {
    	logger.info("Processing report generation...");
        try {
            managedExecutorService.submit(reportTaskFactory.getTask(reportType));
        } catch (RejectedExecutionException ree) {
            return Response
            		.status(Response.Status.SERVICE_UNAVAILABLE)
            		.entity(String.format("%s report task not submitted. %s", reportType.getLabel(), ree.getMessage()))
            		.build();
        }

        return Response
        		.status(Response.Status.OK)
        		.entity(String.format("%s report task successfully submitted.", reportType.getLabel()))
        		.build();
    }

}
