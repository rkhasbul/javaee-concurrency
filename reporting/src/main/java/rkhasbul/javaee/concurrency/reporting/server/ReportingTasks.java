package rkhasbul.javaee.concurrency.reporting.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import rkhasbul.javaee.concurrency.reporting.client.ReportType;

/**
 * Storage for Report tasks
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ReportingTasks {

	private final Map<String, ScheduledFuture<?>> tasks;

	private int counter;
	
    public ReportingTasks() {
        this.tasks = new LinkedHashMap<>();
    }

    @Lock(LockType.WRITE)
    public void put(ReportType reportType, ScheduledFuture<?> scheduledTask) {
        tasks.put(reportType + "-" + ++counter, scheduledTask);
    }

    @Lock(LockType.WRITE)
    public void deleteTask(String scheduledTask) {
    	if (tasks.containsKey(scheduledTask)) {
    		tasks.get(scheduledTask).cancel(true);
        	tasks.remove(scheduledTask);
    	}
    }
    
    @Lock(LockType.READ)
    public ScheduledFuture<?> get(String key) {
        return tasks.get(key);
    }
	
    @Lock(LockType.READ)
    public String getTasks() {
    	return String.join(",", tasks.keySet());
    }
    
    @Lock(LockType.READ)
    public Runnable getTask(final ReportType reportType) {
		switch (reportType) {
			case VAR: return new VarReportTask();
			case MARGIN: return new MarginReportTask();
			default: throw new UnsupportedOperationException();
		}
	}
    
}
