# Java EE Concurrency

Java EE Concurrency is a tutorial that consist of two projects: `var-calculator` and `reporting`.
This tutorial provides overview of Concurrency Utilities for Java EE platform.

## `var-calculator`
 
This section describes a very basic example that shows how to use some of the basic concurrency features in an enterprise application. Specifically, this example uses one of the main components of Concurrency Utilities for Java EE, a [ManagedExecutorService](https://javaee.github.io/javaee-spec/javadocs/javax/enterprise/concurrent/ManagedExecutorService.html) `varExecutor`.

The project demonstrates a scenario where a RESTful web service, exposed as a public API, is used to submit generic VaR calculation jobs for execution. These jobs are processed in the background. Each job prints a "Starting" and a "Finished" message at the beginning and end of the execution. Also, to simulate background processing, each job takes 5 seconds to execute.

The RESTful service exposes two methods:

* /rest/{ticker}/submit: `GET` method that receives a `ticker` path parameter and submits task for execution
* /rest/{ticker}: `GET` method that receives a `ticker` path parameter and search for result in the `varStorage`

`varStorage` is a [Singleton](https://javaee.github.io/javaee-spec/javadocs/javax/ejb/Singleton.html) that contains Java [HashMap](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/HashMap.html) for storing var calculation tasks. It also uses [ConcurrencyManagement](https://javaee.github.io/javaee-spec/javadocs/javax/ejb/ConcurrencyManagement.html) and [Lock](https://javaee.github.io/javaee-spec/javadocs/javax/ejb/Lock.html) annotations for concurrency access.

## `reporting`

The `reporting` project demonstrates how to use Concurrency Utilities for Java EE to schedule tasks for execution. This example provides a JavaServer Faces interface that enables users to submit tasks to be executed by scheduler. The example uses the [ManagedScheduledExecutorService](https://javaee.github.io/javaee-spec/javadocs/javax/enterprise/concurrent/ManagedScheduledExecutorService.html) to schedule tasks for execution after a fixed delay of 30 seconds.

The reporting example consists of the following components:

* A JavaServer Faces page (`index.xhtml`) that contains two elements: a form to submit tasks and a form to cancel scheduled tasks. This page submits Ajax requests to create and cancel tasks.

* A CDI managed bean (`ReportingClient`) that processes the requests from the JavaServer Faces page. This bean invokes the methods in `ReportingService` to submit and cancel scheduled tasks.

* An enterprise bean (`ReportingService`) that obtains executor scheduled service instance using resource injection and submits tasks for execution. This bean is also a JAX-RS web service endpoint.

* A task classes (`MarginReportTask` and `VarReportTask`) that implements the [Runnable](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Runnable.html) interface.
The run method in these classes emulates report generation process and sleeps for 5 and 10 seconds accordingly.

The `ReportingService` class obtains the executor service object from the application server as follows:

```java
@Resource(lookup = "java:jboss/ee/concurrency/scheduler/reporting")
private ManagedScheduledExecutorService reportingExecutor;
```

The `schedule` method in `ReportingService` uses this executor to submit tasks as follows:

```java
final Runnable task = ReportTaskFactory.getTask(reportType);
final ScheduledFuture<?> scheduledTask = reportingExecutor.schedule(
        task, 30, TimeUnit.SECONDS);
tasks.put(reportType.toString() + counter.incrementAndGet(), scheduledTask);
```

For scheduled tasks, `ReportingService` keeps a reference to the [ScheduledFuture](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/concurrent/ScheduledFuture.html) object, so that the user can cancel the task at any time.

## Configure JBoss EAP
 
1. Open the `Administration Console` at http://localhost:9990/console.

2. Go to `Configuration` menu.

3. Expand the `Subsystems -> EE` node, click `View`

4. Expand the `Services -> Executor` node.

5. On the `Executor` page, click `Add` to open the `Add Executor` popup.

6. In the `JNDI Name` field, enter

    > `java:jboss/ee/concurrency/executor/var`
 
7. Click `Edit` and set following settings (keep the default values for other settings):

    * `Core Threads: 2`
    * `Max Threads: 5`
    * `Queue Length: 2`

8. Click `Save`.

9. Expand the `Services -> Scheduled Executor` node.

10. On the `Scheduled Executor` page, click `Add` to open the `Add Scheduled Executor` popup.

11. In the `JNDI Name` field, enter

    > `java:jboss/ee/concurrency/scheduler/reporting`

12. Click `Edit` and set `Core Threads` setting to `2` (keep the default values for other settings)

13. Click `Save`.
