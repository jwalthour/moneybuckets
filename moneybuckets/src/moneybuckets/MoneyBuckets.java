package moneybuckets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import moneybuckets.buckets.chasecreditcard.ChaseCreditCard;
import moneybuckets.reports.SpendingCategoriesReport;

public class MoneyBuckets {

	public static void main(String[] args) {
		// Just for testing right now
		ChaseCreditCard chaseCard = new ChaseCreditCard();
		try {
			// Hardcoded ones for now
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			Date timeRangeStart = dateFormat.parse("2017-12-19");
			Date timeRangeEnd   = dateFormat.parse("2017-12-27");
			
			// Load from CSV
			chaseCard.loadStatement(args[0]);
			chaseCard.loadCatRules("..\\configuration\\base_chase_rules.csv");
			chaseCard.loadCatRules(args[1]);
			
			// Process
			chaseCard.categorizeTransactions();
			List<Map.Entry<String, Double>> totals = chaseCard.getSortedListOfCategoriesAndTotals();
			
			// Output
			System.out.println(totals);
			System.out.println(chaseCard.getUncategorizedTransactions());
			
			// Save
			SpendingCategoriesReport.generateHtmlReport(chaseCard.getTransactions(),
					chaseCard.getOutboundTotalsForCategories(), 
					Paths.get("..", "generated_reports", "spending_report"),
					timeRangeStart, timeRangeEnd);
			
//			SwingUtilities.invokeLater(() -> {
//				ChartShower example = new ChartShower("Expenses by category");
//				example.setChart(SpendingCategoriesReport.getPieChartForExpenseCategories(totals));
//			    example.setSize(800, 400);
//			    example.setLocationRelativeTo(null);
//			    example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//			    example.setVisible(true);
//			  });

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class ChartShower extends JFrame {
		private static final long serialVersionUID = -7620180373407921533L;
		public ChartShower(String title) {
			super(title);
		}
		
		public void setChart(JFreeChart chart) {
		    ChartPanel panel = new ChartPanel(chart);
		    setContentPane(panel);
		}
	}
	
	
}
