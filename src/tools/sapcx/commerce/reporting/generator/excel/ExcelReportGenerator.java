package tools.sapcx.commerce.reporting.generator.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.sapcx.commerce.reporting.generator.ReportGenerator;
import tools.sapcx.commerce.reporting.model.QueryReportConfigurationModel;
import tools.sapcx.commerce.reporting.search.GenericSearchResult;
import tools.sapcx.commerce.reporting.search.GenericSearchResultHeader;

public class ExcelReportGenerator implements ReportGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(ExcelReportGenerator.class);
	private static final String EXCEL_EXTENSION = "xlsx";

	@Override
	public boolean createReport(QueryReportConfigurationModel report, GenericSearchResult result, File file) {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = createSheetWithConfiguration(report, workbook);
			int headerRows = addHeader(report, result, workbook, sheet);
			int dataRows = addResultRows(report, result, workbook, sheet, headerRows);
			int numberOfRows = headerRows + dataRows;
			int numberOfColumns = result.getResultColumns();
			formatWorksheet(report, sheet, numberOfColumns, numberOfRows);

			writeFile(file, workbook);

			return true;
		} catch (IOException e) {
			LOG.error(String.format("Could not write workbook to file: %s", file.getAbsolutePath()), e);
			return false;
		}
	}

	private SXSSFSheet createSheetWithConfiguration(QueryReportConfigurationModel report, SXSSFWorkbook workbook) {
		SXSSFSheet sheet = workbook.createSheet("Report");
		if (shouldHaveColumnsResized(report)) {
			sheet.trackAllColumnsForAutoSizing();
		}
		return sheet;
	}

	private int addHeader(QueryReportConfigurationModel report, GenericSearchResult result, SXSSFWorkbook workbook, SXSSFSheet sheet) {
		CellStyle headerStyle = getStyle(workbook, IndexedColors.BLACK, shouldHaveHighlightedHeader(report), IndexedColors.GREY_40_PERCENT, IndexedColors.BLACK);
		addRow(sheet, 0, result.getHeaderNames(), headerStyle);
		return 1;
	}

	private int addResultRows(QueryReportConfigurationModel report, GenericSearchResult result, SXSSFWorkbook workbook, SXSSFSheet sheet, int headerOffset) {
		CellStyle rowStyleOdd = getStyle(workbook, IndexedColors.BLACK, false, IndexedColors.WHITE, IndexedColors.BLACK);
		CellStyle rowStyleEven = getStyle(workbook, IndexedColors.BLACK, false, IndexedColors.GREY_25_PERCENT, IndexedColors.BLACK);

		List<Map<GenericSearchResultHeader, String>> rowValues = result.getValues();
		int numberOfRows = result.getResultRows();
		for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
			Map<GenericSearchResultHeader, String> rowValue = rowValues.get(rowIndex);
			CellStyle rowStyle = (shouldHaveAlternatingLines(report) && isAlternatingLine(rowIndex)) ? rowStyleEven : rowStyleOdd;
			addRow(sheet, headerOffset + rowIndex, rowValue.values(), rowStyle);
		}
		return numberOfRows;
	}

	private CellStyle getStyle(SXSSFWorkbook workbook, IndexedColors fontColor, boolean fontBold, IndexedColors bgColor, IndexedColors borderColor) {
		Font font = workbook.createFont();
		font.setColor(fontColor.index);
		font.setBold(fontBold);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);

		cellStyle.setFillForegroundColor(bgColor.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setTopBorderColor(borderColor.index);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBottomBorderColor(borderColor.index);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setLeftBorderColor(borderColor.index);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);
		cellStyle.setRightBorderColor(borderColor.index);
		return cellStyle;
	}

	private void addRow(SXSSFSheet sheet, int rowIndex, Collection<String> values, CellStyle style) {
		SXSSFRow row = sheet.createRow(rowIndex);
		Iterator<String> valueIterator = values.iterator();
		for (int columnIndex = 0; valueIterator.hasNext(); columnIndex++) {
			SXSSFCell cell = row.createCell(columnIndex);
			cell.setCellValue(valueIterator.next());
			cell.setCellStyle(style);
		}
	}

	private void formatWorksheet(QueryReportConfigurationModel report, SXSSFSheet sheet, int numberOfColumns, int numberOfRows) {
		if (shouldHaveFrozenHeader(report)) {
			sheet.createFreezePane(0, 1);
		}

		if (shouldHaveFilterActivated(report)) {
			CellRangeAddress range = new CellRangeAddress(0, numberOfRows - 1, 0, numberOfColumns - 1);
			sheet.setAutoFilter(range);
		}

		if (shouldHaveColumnsResized(report)) {
			for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
				if (sheet.isColumnTrackedForAutoSizing(columnIndex)) {
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}

	private void writeFile(File file, SXSSFWorkbook workbook) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			workbook.write(outputStream);
			outputStream.flush();
		}
	}

	@Override
	public String getExtension() {
		return EXCEL_EXTENSION;
	}

	private boolean shouldHaveHighlightedHeader(QueryReportConfigurationModel report) {
		return report.isExcelHighlightHeader();
	}

	private boolean shouldHaveFrozenHeader(QueryReportConfigurationModel report) {
		return report.isExcelFreezeHeader();
	}

	private boolean shouldHaveFilterActivated(QueryReportConfigurationModel report) {
		return report.isExcelActivateFilter();
	}

	private boolean shouldHaveColumnsResized(QueryReportConfigurationModel report) {
		return report.isExcelAutosizeColumns();
	}

	private boolean shouldHaveAlternatingLines(QueryReportConfigurationModel report) {
		return report.isExcelAlternatingLines();
	}

	private boolean isAlternatingLine(int rowIndex) {
		return rowIndex % 2 == 0;
	}
}
