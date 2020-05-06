package rkhasbul.javaee.concurrency.reporting.client;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

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

    private final String serviceEndpoint;

    private final Client client;

    private ReportType reportType;

    private String scheduledTask;

    public ReportingClient() throws UnknownHostException {
        client = ClientBuilder.newClient();
        serviceEndpoint = "http://" + InetAddress.getLocalHost().getHostName() 
                + "/reporting/rest/report";
    }

    public String submit() {
        final Response response = client.target(serviceEndpoint + "/schedule")
                .queryParam("reportType", reportType)
                .request().post(null);
        handleResponse(response);
        return "";
    }

    public String cancelTask() {
        final Response response = client.target(serviceEndpoint + "/cancel/" + scheduledTask).request().delete();
        handleResponse(response);
        return "";
    }

    private void handleResponse(Response response) {
        final String responseMessage = response.readEntity(String.class);
        final FacesMessage message = (response.getStatus() == 200)
                ? new FacesMessage(FacesMessage.SEVERITY_INFO, null, null)
                : new FacesMessage(FacesMessage.SEVERITY_ERROR, null, null);
        message.setSummary(responseMessage);                 
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

    public String[] getScheduledTasks() {
        Response response = client.target(serviceEndpoint + "/tasks").request().get();
        return response.readEntity(String.class).split(",");
    }

    public void setScheduledTasks(String[] scheduledTasks) { }

}