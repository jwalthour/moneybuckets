package moneybuckets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

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
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
