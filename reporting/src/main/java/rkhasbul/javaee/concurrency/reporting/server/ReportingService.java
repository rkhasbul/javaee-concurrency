package rkhasbul.javaee.concurrency.reporting.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;

import rkhasbul.javaee.concurrency.reporting.client.ReportType;

/**
 * Report Generation Service
 *
 * @author Ruslan Khasbulatov
 * @version 1.0
 */

@Startup
@Singleton
@LocalBean
public class ReportingService {

    private static final Logger logger = Logger.getLogger(ReportingService.class.getCanonicalName());

    @Resource(lookup = "java:jboss/ee/concurrency/scheduler/reporting")
    private ManagedScheduledExecutorService reportingExecutor;

    private Map<String, ScheduledFuture<?>> tasks;

    private AtomicLong counter;

    @PostConstruct
    public void init() {
        tasks = new HashMap<>();
        counter = new AtomicLong();
    }

    @PreDestroy
    public void destroy() {
        logger.info("Cancelling scheduled tasks");
        for (ScheduledFuture<?> task : tasks.values()) {
            task.cancel(true);
        }
        reportingExecutor.shutdownNow();
    }

    public boolean schedule(final ReportType reportType) {
        logger.info(String.format("Scheduling %s report task...", reportType.getLabel()));
        try {
            final Runnable task = ReportTaskFactory.getTask(reportType);
            final ScheduledFuture<?> scheduledTask = reportingExecutor.schedule(
                    task, 30, TimeUnit.SECONDS);
            tasks.put(reportType.toString() + counter.incrementAndGet(), scheduledTask);
        } catch (RejectedExecutionException ree) {
            logger.severe(String.format("%s report task not been scheduled. %s", reportType.getLabel(), ree.getMessage()));
            return false;
        }
        logger.info(String.format("%s report task successfully scheduled.", reportType.getLabel()));
        return true;
    }

    public Set<String> getTasks() {
        logger.info("Providing list of scheduled tasks...");
        return tasks.keySet();
    }

    public boolean cancel(final String scheduledTask) {
        logger.info(String.format("Cancelling '%s' scheduled task...", scheduledTask));
        if (tasks.containsKey(scheduledTask)) {
            tasks.get(scheduledTask).cancel(true);
            tasks.remove(scheduledTask);
            return true;
        }
        return false;
    }

}