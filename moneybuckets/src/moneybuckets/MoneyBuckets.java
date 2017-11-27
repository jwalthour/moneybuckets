package moneybuckets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class MoneyBuckets {

	public static void main(String[] args) {
		// Just for testing right now
		DataStore ds = new DataStore();
		PaymentCategorizer cat = new PaymentCategorizer();
		try {
			// Load from CSV
			ds.LoadChaseCreditCardStatement(args[0]);
			cat.LoadCategorizationRules(args[1]);
			
			// Process
			cat.CategorizeTransactions(ds.GetAllTransactions());
			HashMap<String, Double> totals = cat.GetOutboundTotalsForCategories(ds.GetAllTransactions());
			
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
