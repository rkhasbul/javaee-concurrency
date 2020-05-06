package rkhasbul.javaee.concurrency.reporting.client;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import rkhasbul.javaee.concurrency.reporting.server.ReportingService;

/**
 * Reporting UI bean
 *
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
@Named
@RequestScoped
public class ReportingClient implements Serializable {

    /**
     * Serial Version Unique Identifier
     */
    private static final long serialVersionUID = 8252587936943995392L;

    private static final Logger logger = Logger.getLogger(ReportingClient.class.getCanonicalName());

    @EJB
    private ReportingService service;

    private ReportType reportType;

    private String scheduledTask;

    public String submit() {
        final boolean success = service.schedule(reportType);
        handleResponse(success);
        return "";
    }

    public String cancelTask() {
        final boolean response = service.cancel(scheduledTask);
        handleResponse(response);
        return "";
    }

    private void handleResponse(final boolean success) {
        final FacesMessage message = success
                ? new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Request has been processed successfully", null)
                : new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Request has been processed with error", null);
        FacesContext.getCurrentInstance().addMessage(null, message);
        logger.info(message.getSummary());
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportType[] getReportTypes() {
        return ReportType.values();
    }

    public void setReportTypes(ReportType[] reportTypes) { }

    public String getScheduledTask() {
        return scheduledTask;
    }

    public void setScheduledTask(String scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public Set<String> getScheduledTasks() {
        return service.getTasks();
    }

    public void setScheduledTasks(Set<String> scheduledTasks) { }

}