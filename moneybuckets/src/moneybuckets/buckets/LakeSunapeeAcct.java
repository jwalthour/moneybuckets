package moneybuckets.buckets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import moneybuckets.Bucket;
import moneybuckets.Transaction;
import moneybuckets.TransactionCategorizer;

public class LakeSunapeeAcct extends Bucket {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

	private List<Transaction> transactions = new LinkedList<>();
	private TransactionCategorizer cat = new TransactionCategorizer();
	public LakeSunapeeAcct() {
		super("Lake Sunapee account", false);
	}

	public LakeSunapeeAcct(String informal_name) {
		super("Lake Sunapee " + informal_name, false);
	}
	

	public void loadStatement(String path) throws FileNotFoundException, IOException {
		// Note: Lake Sunapee statements also include a "Serial Number" column.
		// For checks, this is the check number.  For mobile deposits, it appears to be some other
		// kind of identifier.  I've left this field out, because it doesn't appear to offer any
		// information useful to moneybuckets.  Check numbers also appear in their descriptions.
		
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
//			System.out.println(record);
			String amt_str = record.get("Amount");
			double amt = Double.parseDouble(amt_str);
			Date date = null;
			try {
				date = DATE_FORMAT.parse(record.get("Posted Date"));
			} catch (ParseException e) {
				// do nothing
			}
			// Populate what bucket information we have
			Bucket source, dest;
			if(record.get("CR/DR").equalsIgnoreCase("DR")) {
				// Debit ... R?
				source = this;
				dest   = Bucket.getExternalBucket();
			} else if (record.get("CR/DR").equalsIgnoreCase("CR")) {
				// CRedit?
				if(record.get("Description").equalsIgnoreCase("Interest Deposit")) {
					source = getInstitutionalBucket();					
				} else {
					source = null; // unknown
				}
				dest = this;
			} else {
				source = null; // unknown
				dest   = null; // unknown
			}
			Transaction tr = new Transaction(source, dest, record.get("CR/DR").toUpperCase(), record.get("Description"), amt, date);
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
	
	// Singleton bucket to represent the financial institution
	private static Bucket chase = null;
	public static Bucket getInstitutionalBucket() {
		if(chase == null) {
			chase = new Bucket("Lake Sunapee Bank");
		}
		return chase;
	}
		
}
