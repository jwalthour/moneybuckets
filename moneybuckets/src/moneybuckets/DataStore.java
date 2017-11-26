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
import java.util.List;
import org.apache.commons.csv.*;

public class DataStore {
	private List<Bucket> buckets;
	private List<Transaction> transactions;
	
	public void LoadBucketsFromFile(String path) {
		
	}
	
	public void LoadChaseCreditCardStatement(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			System.out.println(record);
		}
	}

	public void LoadLakeSunapeeBankStatement(String path) {
		
	}
}
