package moneybuckets;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MoneyBuckets {

	public static void main(String[] args) {
		// Just for testing right now
		DataStore ds = new DataStore();
		PaymentCategorizer cat = new PaymentCategorizer();
		try {
			ds.LoadChaseCreditCardStatement(args[0]);
			cat.LoadCategorizationRules(args[1]);
			ds.CategorizeTransactions(cat);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
