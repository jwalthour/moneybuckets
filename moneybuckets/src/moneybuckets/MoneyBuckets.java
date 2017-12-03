package moneybuckets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import moneybuckets.buckets.ChaseCreditCardBucket;

public class MoneyBuckets {

	public static void main(String[] args) {
		// Just for testing right now
		ChaseCreditCardBucket chaseCard = new ChaseCreditCardBucket();
		try {
			// Load from CSV
			chaseCard.loadStatement(args[0]);
			chaseCard.loadCatRules("..\\configuration\\base_chase_rules.csv");
			chaseCard.loadCatRules(args[1]);
			
			// Process
			chaseCard.categorizeTransactions();
			HashMap<String, Double> totals = chaseCard.getOutboundTotalsForCategories();
			
			// Output
			System.out.println(totals);
			System.out.println(chaseCard.getUncategorizedTransactions());
			
			SwingUtilities.invokeLater(() -> {
				ChartShower example = new ChartShower("Expenses by category");
				example.setChart(getPieChartForExpenseCategories(totals));
			    example.setSize(800, 400);
			    example.setLocationRelativeTo(null);
			    example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			    example.setVisible(true);
			  });

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
	
	public static JFreeChart getPieChartForExpenseCategories(HashMap<String, Double> totals) {
		DefaultPieDataset dataset= new DefaultPieDataset();
		
		for(String cat : totals.keySet()) {
			dataset.setValue(cat, -totals.get(cat));
		}
		
		JFreeChart chart = ChartFactory.createPieChart(
		        "Expenses by category",
		        dataset,
		        false, 
		        true,
		        false);
		PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
				"Category {0} : {1}", new DecimalFormat("$0.00"), new DecimalFormat("0%"));
		((PiePlot) chart.getPlot()).setLabelGenerator(labelGenerator);
	    return chart;
	}
}
