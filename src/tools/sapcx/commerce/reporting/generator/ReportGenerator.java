package tools.sapcx.commerce.reporting.generator;

import java.io.File;

import tools.sapcx.commerce.reporting.model.QueryReportConfigurationModel;
import tools.sapcx.commerce.reporting.search.GenericSearchResult;

public interface ReportGenerator {
	boolean createReport(QueryReportConfigurationModel report, GenericSearchResult searchResult, File file);

	String getExtension();
}
