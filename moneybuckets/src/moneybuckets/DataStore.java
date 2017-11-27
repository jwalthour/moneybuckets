/*
 * Stores a list of transactions to be analyzed, as well as data on
 * buckets.  Also contains methods for loading said data to and from
 * files. 
 * 
 */
package moneybuckets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.csv.*;

public class DataStore {
	// TODO: This needs to be generalized somehow so we can have a whole set of them
	private Bucket chaseBucket = new Bucket("Chase credit card");
	private Bucket externalBucket = new Bucket("Outside", true);
	// TODO: This should be both generalized and globalish, keeping in mind one transaction may go between buckets.
	private List<Transaction> chaseTransactions = new LinkedList<>();
	
	OK, the big problem at the moment is that payments, fees, and sales are all run through the same filters
	
	public void LoadBucketsFromFile(String path) {
		
	}
	
	public void LoadChaseCreditCardStatement(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
//			System.out.println(record);
			String amt_str = record.get(record.size() - 1); // There's something not quite 
			
			double amt = Double.parseDouble(amt_str);
			// TODO: parse date
			Transaction tr = new Transaction(chaseBucket, externalBucket, record.get("Description"), amt, null);
			chaseTransactions.add(tr);
		}
	}

	public void LoadLakeSunapeeBankStatement(String path) {
		
	}
	
	public List<Transaction> GetAllTransactions() {
		return chaseTransactions;
	}
}
