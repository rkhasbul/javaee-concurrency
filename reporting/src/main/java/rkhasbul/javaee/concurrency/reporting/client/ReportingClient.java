package rkhasbul.javaee.concurrency.reporting.client;

import java.io.Serializable;
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
	
	private static final String serviceEndpoint = "http://localhost:8080/reporting/rest/reporting-service/process";

	private ReportType reportType;
	
    public String submit() {
        final Client client = ClientBuilder.newClient();

        final Response response = client.target(serviceEndpoint)
                .queryParam("reportType", reportType)
                .request()
                .post(null);

        final String responseMessage = response.readEntity(String.class);
        final FacesMessage message = (response.getStatus() == 200)
                ? new FacesMessage(FacesMessage.SEVERITY_INFO, null, null)
                : new FacesMessage(FacesMessage.SEVERITY_ERROR, null, null);
        message.setSummary(responseMessage);                 
        FacesContext.getCurrentInstance().addMessage(null, message);
        logger.info(message.getSummary());
        return "";
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
	
}