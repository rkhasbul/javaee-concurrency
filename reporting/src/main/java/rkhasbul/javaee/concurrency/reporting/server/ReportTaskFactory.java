package rkhasbul.javaee.concurrency.reporting.server;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import rkhasbul.javaee.concurrency.reporting.client.ReportType;

/**
 * Report Task factory
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 *
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ReportTaskFactory {

	public Runnable getTask(final ReportType reportType) {
		switch (reportType) {
			case VAR: return new VarReportTask();
			case MARGIN: return new MarginReportTask();
			default: throw new UnsupportedOperationException();
		}
	}
	
}
