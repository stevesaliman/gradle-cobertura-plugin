package net.saliman.gradle.plugin.cobertura;

import net.saliman.gradle.plugin.cobertura.CoberturaReports;
import org.gradle.api.Task;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.reporting.SingleFileReport;
import org.gradle.api.reporting.internal.TaskGeneratedSingleFileReport;
import org.gradle.api.reporting.internal.TaskReportContainer;

public class CoberturaReportsImpl extends TaskReportContainer<SingleFileReport> implements CoberturaReports {

    public CoberturaReportsImpl(Task task, CollectionCallbackActionDecorator collectionCallbackActionDecorator) {
        super(SingleFileReport.class, task, collectionCallbackActionDecorator);

        add(TaskGeneratedSingleFileReport.class, "html", task);
    }

    public SingleFileReport getHtml() {
        return getByName("html");
    }
}
