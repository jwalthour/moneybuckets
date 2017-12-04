package moneybuckets.reports;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

public class SpendingCategoriesReport {
	private static final String BASE_REPORT_NAME = "report.html";
	private static final String STATIC_CSS_NAME = "style.css";
	private static final String CATEGORIES_PIE_CHART_FILE = "cat_pie.png";
	private static final int CATEGORIES_PIE_CHART_WIDTH_PX  = 800;
	private static final int CATEGORIES_PIE_CHART_HEIGHT_PX = 400;
	/**
	 * 
	 * @param totals - a sorted list of category strings and totals for each category
	 * @param reportSavePath - base folder to which to save report.  Will contain report.html and one or more PNG files after running.
	 * @throws IOException - thrown when file can't be written
	 */
	public static void generateHtmlReport(List<Map.Entry<String, Double>> totals, Path reportSavePath) throws IOException {
		// Make sure folder exists
		new File(reportSavePath.toString()).mkdirs();
		
		// TODO: Copy in CSS file
		
		// Save off a PNG of the main pie chart
		FileOutputStream pieChartFile = new FileOutputStream(reportSavePath.resolve(CATEGORIES_PIE_CHART_FILE).toFile());
		JFreeChart mainPieChart = getPieChartForExpenseCategories(totals);
		ChartUtilities.writeChartAsPNG(pieChartFile, mainPieChart, CATEGORIES_PIE_CHART_WIDTH_PX, CATEGORIES_PIE_CHART_HEIGHT_PX);
		
		// Open top-level HTML file
		FileOutputStream htmlFile = new FileOutputStream(reportSavePath.resolve(BASE_REPORT_NAME).toFile());
		writeHtml(htmlFile);
	}
	
	private static void writeHtml(FileOutputStream htmlFile) throws IOException {
		// I'm aware Java has HTML templating libraries.  I feel they are entirely too heavy for this use case, since they're meant for server-side use.
		String header = "<html><head><style>body { font-family: Arial; }</style></head><body>";
		htmlFile.write(header.getBytes());
		
		htmlFile.write("<h1>Spending by category</h1>".getBytes());
		
		String pieChartHolder = "<p><img src=\"" + CATEGORIES_PIE_CHART_FILE + "\" width=" + CATEGORIES_PIE_CHART_WIDTH_PX + " height=" + CATEGORIES_PIE_CHART_HEIGHT_PX +" /></p>";
		htmlFile.write(pieChartHolder.getBytes());
		
		// TODO: Table?
		
		// TODO: unknowns
		
		String footer = "</body></html>";
		htmlFile.write(footer.getBytes());
		
	}
	
	public static JFreeChart getPieChartForExpenseCategories(List<Map.Entry<String, Double>> totals) {
		DefaultPieDataset dataset= new DefaultPieDataset();
		
		for(Map.Entry<String, Double> pair : totals) {
			dataset.setValue(pair.getKey(), -pair.getValue());
		}
		
		JFreeChart chart = ChartFactory.createPieChart(
		        "",
		        dataset,
		        false, 
		        true,
		        false);
		PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
				"Category {0} : {1}", new DecimalFormat("$0.00"), new DecimalFormat("0%"));
		((PiePlot) chart.getPlot()).setLabelGenerator(labelGenerator);
		((PiePlot)chart.getPlot()).setDirection(Rotation.ANTICLOCKWISE);
		((PiePlot)chart.getPlot()).setBackgroundPaint(new Color(255,255,255));
	    return chart;
	}

}