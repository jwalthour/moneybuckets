package moneybuckets.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import moneybuckets.Transaction;

public class NetCashflowReport {
	private static final String BASE_REPORT_NAME = "cashflow_report.html";
	private static final String STATIC_CSS_NAME = "style.css";
	private static final String CATEGORIES_PIE_CHART_FILE = "net_cashflow.png";
	private static final int CATEGORIES_PIE_CHART_WIDTH_PX  = 800;
	private static final int CATEGORIES_PIE_CHART_HEIGHT_PX = 400;
	private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat("$0.00");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

	public static void generateHtmlReport(List<Entry<String, Double>> incomeTotals, List<Entry<String, Double>> expenseTotals,  FileOutputStream htmlFile, String headline, String barChartFilename) throws IOException {


	}

	public static void generateCashflowBarChart(FileOutputStream barChartFile, List<Entry<String, Double>> incomeTotals, List<Entry<String, Double>> expenseTotals) throws IOException {
		JFreeChart mainBarChart = getBarChartForExpenseCategories(incomeTotals, expenseTotals);
		ChartUtilities.writeChartAsPNG(barChartFile, mainBarChart, CATEGORIES_PIE_CHART_WIDTH_PX, CATEGORIES_PIE_CHART_HEIGHT_PX);
	}
	public static JFreeChart getBarChartForExpenseCategories(List<Map.Entry<String, Double>> incomeTotals, List<Map.Entry<String, Double>> expenseTotals) {
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		for (Map.Entry<String, Double> entry : incomeTotals) {
			ds.addValue(entry.getValue(), entry.getKey(), "Income");			
		}
		for (Map.Entry<String, Double> entry : expenseTotals) {
			ds.addValue(entry.getValue(), entry.getKey(), "Expenses");			
		}
		
		final JFreeChart chart = ChartFactory.createStackedBarChart(
		            "Income vs expenses",  // chart title
		            "",                  // domain axis label
		            "",                     // range axis label
		            ds,                     // data
		            PlotOrientation.HORIZONTAL,    // the plot orientation
		            true,                        // legend
		            true,                        // tooltips
		            false                        // urls
		        );
		 return chart;
	}
}
