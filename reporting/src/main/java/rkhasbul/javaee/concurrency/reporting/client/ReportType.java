package rkhasbul.javaee.concurrency.reporting.client;

/**
 * Types of report enumeration
 *
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
public enum ReportType {

    VAR("VaR"), MARGIN("Margin");

    private final String label;

    private ReportType(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}