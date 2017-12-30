package moneybuckets.buckets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import moneybuckets.Bucket;
import moneybuckets.Transaction;
import moneybuckets.TransactionCategorizer;

public class ChaseCreditCard extends Bucket {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
	
	private List<Transaction> transactions = new LinkedList<>();
	private TransactionCategorizer cat = new TransactionCategorizer();
	public ChaseCreditCard() {
		super("Chase Credit Card", false);
	}

	public void loadStatement(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
//			System.out.println(record);
			String amt_str = record.get(record.size() - 1); // For some reason the description is sometimes several columns.  I think that's due to commas in descriptions.  But the amount is always the last column.
			double amt = Double.parseDouble(amt_str);
			Date date = null;
			try {
				date = DATE_FORMAT.parse(record.get(1));
			} catch (ParseException e) {
				// do nothing
			}
			Transaction tr = new Transaction(this, Bucket.getExternalBucket(), record.get("Type").toUpperCase(), record.get("Description"), amt, date);
			transactions.add(tr);
		}
	}

	public void loadCatRules(String path) throws FileNotFoundException, IOException {
		cat.loadRules(path);
	}
	
	public void categorizeTransactions() {
		cat.categorizeTransactions(transactions);
	}
	
	public List<Transaction> getTransactions() {
		return (List<Transaction>)transactions;
	}
		
	public List<Map.Entry<String, Double>>  getSortedListOfCategoriesAndTotals() {
		HashMap<String, Double> totalForCat = getOutboundTotalsForCategories();
		List<Map.Entry<String, Double>> list = new LinkedList<>(totalForCat.entrySet());
		list.sort(new Comparator<Map.Entry<String, Double>>() {

			@Override
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
		       if (o1.getValue() > o2.getValue()) {
		           return 1;
		       } else if (o1.getValue() < o2.getValue()){
		           return -1;
		       } else {
		           return 0;
		       }
			}
		});
		return list;
	}
	
	public HashMap<String, Double> getOutboundTotalsForCategories() {
		HashMap<String, Double> totalForCat = new HashMap<String, Double>();
//		System.out.println(transactions);
		
		for (Transaction tr : transactions) {
			String cat = tr.getCategory();
			if(tr.getType() != "PAYMENT") {
				if(totalForCat.containsKey(cat)) {
					// Not the first transaction
					totalForCat.put(cat, totalForCat.get(cat) + tr.getAmount());
				} else {
					// Very first transaction
					totalForCat.put(cat, tr.getAmount());
				}
			}
		}
		return totalForCat;
	}
	
	public List<Transaction> getUncategorizedTransactions() {
		List<Transaction> uncat = new LinkedList<>();
		
		for (Transaction tr : transactions) {
			if(tr.getCategory().equalsIgnoreCase(Transaction.UNCATEGORIZED)) {
				uncat.add(tr);
			}
		}		
		return uncat;
	}
}
