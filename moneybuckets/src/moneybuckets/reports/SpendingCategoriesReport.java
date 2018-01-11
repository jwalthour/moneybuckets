package moneybuckets.reports;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

import moneybuckets.Transaction;

public class SpendingCategoriesReport {
	private static final int CATEGORIES_PIE_CHART_WIDTH_PX  = 800;
	private static final int CATEGORIES_PIE_CHART_HEIGHT_PX = 400;
	private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat("$0.00");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

	
	/**
	 * 
	 * @param totals - a sorted list of category strings and totals for each category
	 * @param reportSavePath - base folder to which to save report.  Will contain report.html and one or more PNG files after running.
	 * @throws IOException - thrown when file can't be written
	 */
	
	public static void generateHtmlReport(List<Transaction> categorizedTransactions, HashMap<String, Double> catTotals,  FileOutputStream htmlFile, String headline, String pieChartFilename) throws IOException {

		// Sort transactions
		categorizedTransactions.sort(new Transaction.ComparatorUncatCatByTotalDescAmount(catTotals));
		
		writeHtml(htmlFile, categorizedTransactions, headline, pieChartFilename);
	}
	
	public static void generateCatPieChart(FileOutputStream pieChartFile, HashMap<String, Double> catTotals) throws IOException {
		JFreeChart mainPieChart = getPieChartForExpenseCategories(catTotals);
		ChartUtilities.writeChartAsPNG(pieChartFile, mainPieChart, CATEGORIES_PIE_CHART_WIDTH_PX, CATEGORIES_PIE_CHART_HEIGHT_PX);
	}
	

	private static void writeHtml(FileOutputStream htmlFile, List<Transaction> categorizedTransactions, String headline, String pieChartFilename) throws IOException {
		// I'm aware Java has HTML templating libraries.  I feel they are entirely too heavy for this use case, since they're meant for server-side use.
		String header = "<html><head><style>body { font-family: Arial; }</style></head><body>";
		htmlFile.write(header.getBytes());
		
		String title = "<h1>" + headline + "</h1>";
		htmlFile.write(title.getBytes());
		
		htmlFile.write("<h2>Summary</h2>".getBytes());
		String pieChartHolder = "<p><img src=\"" + pieChartFilename + "\" width=" + CATEGORIES_PIE_CHART_WIDTH_PX + " height=" + CATEGORIES_PIE_CHART_HEIGHT_PX +" /></p>";
		htmlFile.write(pieChartHolder.getBytes());
		
		// Table of transactions
		htmlFile.write("<h2>Individual transactions</h2>".getBytes());
		htmlFile.write("<table>".getBytes());
		for (Transaction transaction : categorizedTransactions) {
			htmlFile.write("<tr><td>".getBytes());
			htmlFile.write(DATE_FORMAT.format(transaction.getTimestamp()).getBytes());
			htmlFile.write("</td><td>".getBytes());
			htmlFile.write(transaction.getDescription().getBytes());
			htmlFile.write("</td><td>".getBytes());
			htmlFile.write(CURRENCY_FORMATTER.format(transaction.getAmount()).getBytes());
			htmlFile.write("</td><td>".getBytes());
			htmlFile.write(transaction.getCategory().getBytes());
			htmlFile.write("</td></tr>".getBytes());
		}
		htmlFile.write("</table>".getBytes());
		
		String footer = "</body></html>";
		htmlFile.write(footer.getBytes());
		
	}

	public static JFreeChart getPieChartForExpenseCategories(HashMap<String, Double> totals) {
		List<Map.Entry<String, Double>> list = new LinkedList<>(totals.entrySet());
		list.sort(new Comparator<Map.Entry<String, Double>>() {

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
		});
		
		return getPieChartForExpenseCategories(list);
	}
	
	public static JFreeChart getPieChartForExpenseCategories(List<Map.Entry<String, Double>> totals) {
		DefaultPieDataset dataset= new DefaultPieDataset();
		
		for(Map.Entry<String, Double> pair : totals) {
			dataset.setValue(pair.getKey(), pair.getValue());
		}
		
		JFreeChart chart = ChartFactory.createPieChart(
		        "",
		        dataset,
		        false, 
		        true,
		        false);
		PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
				"Category {0} : {1}", CURRENCY_FORMATTER, new DecimalFormat("0%"));
		((PiePlot) chart.getPlot()).setLabelGenerator(labelGenerator);
		((PiePlot)chart.getPlot()).setDirection(Rotation.ANTICLOCKWISE);
		((PiePlot)chart.getPlot()).setBackgroundPaint(new Color(255,255,255));
	    return chart;
	}

}
