package rkhasbul.javaee.concurrency.reporting.server;

import rkhasbul.javaee.concurrency.reporting.client.ReportType;

/**
 * Report Task factory
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 *
 */
public final class ReportTaskFactory {

	private ReportTaskFactory() {}
	
	public static Runnable getTask(final ReportType reportType) {
		switch (reportType) {
			case VAR: return new VarReportTask();
			case MARGIN: return new MarginReportTask();
			default: throw new UnsupportedOperationException();
		}
	}
	
}
