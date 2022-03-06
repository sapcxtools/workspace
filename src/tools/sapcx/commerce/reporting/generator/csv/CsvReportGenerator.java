package tools.sapcx.commerce.reporting.generator.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.sapcx.commerce.reporting.generator.ReportGenerator;
import tools.sapcx.commerce.reporting.model.QueryReportConfigurationModel;
import tools.sapcx.commerce.reporting.search.GenericSearchResult;
import tools.sapcx.commerce.reporting.search.GenericSearchResultHeader;

public class CsvReportGenerator implements ReportGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(CsvReportGenerator.class);
	private static final String CSV_EXTENSION = "csv";

	@Override
	public boolean createReport(QueryReportConfigurationModel report, GenericSearchResult result, File file) {
		CsvReportWriter csvWriter = null;
		try {
			List<GenericSearchResultHeader> headers = result.getHeaders();
			csvWriter = new CsvReportWriter(file, report);
			addRow(csvWriter, result.getHeaderNames());

			List<Map<GenericSearchResultHeader, String>> rowValues = result.getValues();
			List<String> values = new ArrayList<>(result.getResultColumns());

			int numberOfRows = result.getResultRows();
			for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
				Map<GenericSearchResultHeader, String> rowValue = rowValues.get(rowIndex);
				for (GenericSearchResultHeader header : headers) {
					values.add(rowValue.get(header));
				}
				addRow(csvWriter, values);
				values.clear();
			}

			return true;
		} catch (IOException e) {
			LOG.error(String.format("Could not write CSV to file: %s", file.getAbsolutePath()), e);
			return false;
		} finally {
			if (csvWriter != null) {
				csvWriter.closeQuietly();
			}
		}
	}

	private void addRow(CsvReportWriter csvWriter, Collection<String> values) throws IOException {
		Map<Integer, String> csvLine = new LinkedHashMap<>();
		Iterator<String> valueIterator = values.iterator();
		for (int columnIndex = 0; valueIterator.hasNext(); columnIndex++) {
			csvLine.put(columnIndex, valueIterator.next());
		}
		csvWriter.write(csvLine);
	}

	@Override
	public String getExtension() {
		return CSV_EXTENSION;
	}
}
