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

	public static void generateHtmlReport(List<Entry<String, Double>> incomeTotals, List<Entry<String, Double>> expenseTotals, Path reportSavePath, Date timeRangeStart, Date timeRangeEnd) throws IOException {
		// Make sure folder exists
		new File(reportSavePath.toString()).mkdirs();
		
		// Save off a PNG of the main pie chart
		FileOutputStream pieChartFile = new FileOutputStream(reportSavePath.resolve(CATEGORIES_PIE_CHART_FILE).toFile());
		JFreeChart mainPieChart = getPieChartForExpenseCategories(incomeTotals, expenseTotals);
		ChartUtilities.writeChartAsPNG(pieChartFile, mainPieChart, CATEGORIES_PIE_CHART_WIDTH_PX, CATEGORIES_PIE_CHART_HEIGHT_PX);
		
		double totalIncome = 0, totalExpense = 0;
		for (Map.Entry<String, Double> entry : incomeTotals) {
			totalIncome += entry.getValue();			
		}
		for (Map.Entry<String, Double> entry : expenseTotals) {
			totalExpense += entry.getValue();			
		}

		System.out.println("Total income: " + totalIncome + " Total expenses: " + totalExpense);
	}
	private static JFreeChart getPieChartForExpenseCategories(HashMap<String, Double> incomeTotals,
			HashMap<String, Double> expenseTotals) {
		List<Map.Entry<String, Double>> incomeList  = new LinkedList<>(incomeTotals .entrySet());
		List<Map.Entry<String, Double>> expenseList = new LinkedList<>(expenseTotals.entrySet());
		
		Comparator<Map.Entry<String, Double>> sorter = new Comparator<Map.Entry<String, Double>>() {

			@Override
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
		       if (o1.getValue() > o2.getValue()) {
		           return 1;
		       } else if (o1.getValue() < o2.getValue()){
		           return -1;
		       } else {
		           return 0;
		       }
			}
		};
		
		incomeList .sort(sorter);
		expenseList.sort(sorter);
		
		return getPieChartForExpenseCategories(incomeList, expenseList);
	}
	
	public static JFreeChart getPieChartForExpenseCategories(List<Map.Entry<String, Double>> incomeTotals, List<Map.Entry<String, Double>> expenseTotals) {
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
