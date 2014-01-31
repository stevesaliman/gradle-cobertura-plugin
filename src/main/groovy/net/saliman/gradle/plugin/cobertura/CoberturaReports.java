package net.saliman.gradle.plugin.cobertura;

import org.gradle.api.reporting.ReportContainer;
import org.gradle.api.reporting.SingleFileReport;

/**
 * The reporting configuration for the GenerateReportTask task.
 */
public interface CoberturaReports extends ReportContainer<SingleFileReport> {
    /**
     * The Cobertura html report
     *
     * @return The Cobertura text report
     */
    SingleFileReport getHtml();
}
