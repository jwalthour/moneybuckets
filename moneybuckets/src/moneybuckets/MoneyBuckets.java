package moneybuckets;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MoneyBuckets {

	public static void main(String[] args) {
		// Just for testing right now
		DataStore ds = new DataStore();
		try {
			ds.LoadChaseCreditCardStatement(args[0]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
