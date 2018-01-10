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

import moneybuckets.analysis.TransferDetector;
import moneybuckets.buckets.ChaseCreditCard;
import moneybuckets.buckets.LakeSunapeeAcct;
import moneybuckets.reports.NetCashflowReport;
import moneybuckets.reports.SpendingCategoriesReport;

public class MoneyBuckets {

	public static void main(String[] args) {
		// Just for testing right now
		ChaseCreditCard chaseCard = new ChaseCreditCard();
		LakeSunapeeAcct checkingAcct = new LakeSunapeeAcct("checking");
		LakeSunapeeAcct savingsAcct = new LakeSunapeeAcct("savings");
		TransactionCategorizer expenseCat = new TransactionCategorizer();
		TransactionCategorizer incomeCat = new TransactionCategorizer();
		TransferDetector td = new TransferDetector();
		try {
			// Hardcoded ones for now
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			Date timeRangeStart = dateFormat.parse("2017-11-2");
			Date timeRangeEnd   = dateFormat.parse("2017-12-2");
			
			// Load from CSV
			chaseCard.loadStatement(args[0]);
			expenseCat.loadRules("..\\configuration\\base_expense_rules.csv");
			expenseCat.loadRules(args[1]);
			checkingAcct.loadStatement(args[2]);
			incomeCat.loadRules("..\\configuration\\base_income_rules.csv");
			td.loadRules("..\\configuration\\transfer_rules.csv");
			
			// Process
			List<Transaction> expenses = new LinkedList<>();
			expenses.addAll(chaseCard.getExpenses(timeRangeStart, timeRangeEnd));
			expenses.addAll(checkingAcct.getExpenses(timeRangeStart, timeRangeEnd));
			expenses.addAll(savingsAcct.getExpenses(timeRangeStart, timeRangeEnd));
			expenseCat.categorizeTransactions(expenses);
			List<Transaction> income = new LinkedList<>();
			income.addAll(chaseCard.getIncomes(timeRangeStart, timeRangeEnd));
			income.addAll(checkingAcct.getIncomes(timeRangeStart, timeRangeEnd));
			income.addAll(savingsAcct.getIncomes(timeRangeStart, timeRangeEnd));
			incomeCat.categorizeTransactions(income);
			List<Transaction> transfers = td.detectTransfers(income, expenses);

			List<Map.Entry<String, Double>> expenseTotals = TransactionCategorizer.GetSortedListOfCategoriesAndTotals(expenses);
			List<Map.Entry<String, Double>> incomeTotals  = TransactionCategorizer.GetSortedListOfCategoriesAndTotals(income  );
			System.out.println(expenseTotals);
			System.out.println(incomeTotals);
			
			NetCashflowReport.generateHtmlReport(incomeTotals, expenseTotals, Paths.get("..", "generated_reports", "cashflow_report"), timeRangeStart, timeRangeEnd);
			
			// Output
//			System.out.println(totals);
//			System.out.println(chaseCard.getUncategorizedTransactions());
//			System.out.println(checkingAcct.getTransactions());
			
			// Save
			SpendingCategoriesReport.generateHtmlReport(expenses,
					TransactionCategorizer.GetTotalsForCategories(expenses), 
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
