package moneybuckets.buckets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import moneybuckets.Bucket;
import moneybuckets.Transaction;
import moneybuckets.buckets.ChaseCreditCardTransaction.TransactionType;

public class ChaseCreditCardBucket extends Bucket {
	private List<ChaseCreditCardTransaction> transactions = new LinkedList<>();
	private ChaseCreditCardPaymentCategorizer cat = new ChaseCreditCardPaymentCategorizer();
	
	public ChaseCreditCardBucket() {
		super("Chase Credit Card", false);
	}

	public void loadStatement(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
//			System.out.println(record);
			String amt_str = record.get(record.size() - 1); // There's something not quite 
			ChaseCreditCardTransaction.TransactionType transType = TransactionType.SALE;
			try {
				transType = ChaseCreditCardTransaction.TransactionType.valueOf(record.get("Type"));
			} catch (IllegalArgumentException e) {
				// do nothing
			}
			double amt = Double.parseDouble(amt_str);
			// TODO: parse date
			ChaseCreditCardTransaction tr = new ChaseCreditCardTransaction(this, Bucket.getExternalBucket(), record.get("Description"), amt, null, transType);
			transactions.add(tr);
		}
	}

	public void loadCatRules(String path) throws FileNotFoundException, IOException {
		cat.loadRules(path);
	}
	
	public void categorizeTransactions() {
		cat.categorizeTransactions(transactions);
	}
	
	public HashMap<String, Double> getOutboundTotalsForCategories() {
		return cat.getOutboundTotalsForCategories(transactions);
	}
}
