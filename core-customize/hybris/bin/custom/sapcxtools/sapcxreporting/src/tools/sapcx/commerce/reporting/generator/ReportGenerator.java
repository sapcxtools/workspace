package tools.sapcx.commerce.reporting.generator;

import java.io.File;

import tools.sapcx.commerce.reporting.report.data.QueryFileConfigurationData;
import tools.sapcx.commerce.reporting.search.GenericSearchResult;

public interface ReportGenerator {
	boolean createReport(QueryFileConfigurationData report, GenericSearchResult searchResult, File file);

	String getExtension();
}
