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
	
	public ChaseCreditCard() {
		super("Chase Credit Card", false);
	}

	public void loadStatement(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
//			System.out.println(record);
			String amt_str = record.get(record.size() - 1); // For some reason the description is sometimes several columns.  I think that's due to commas in descriptions.  But the amount is always the last column.
			double amt_raw = Double.parseDouble(amt_str);
			double amt_pos = Math.abs(amt_raw);
			Date date = null;
			try {
				date = DATE_FORMAT.parse(record.get(1));
			} catch (ParseException e) {
				// do nothing
			}
			// Populate what bucket information we have
			Bucket source, dest;
			if(record.get("Type").equalsIgnoreCase("Sale")) {
				// Normal purchases
				source = this;
				dest   = Bucket.getExternalBucket();
			} else if (record.get("Type").equalsIgnoreCase("Payment")) {
				// Payments toward the balance
				source = null; // unknown
				dest   = this;
			} else if (record.get("Type").equalsIgnoreCase("Fee")) {
				// Interest and presumably other charges
				source = this;
				dest   = getInstitutionalBucket();
			} else if (record.get("Type").equalsIgnoreCase("Adjustment")) {
				// Redeemed rewards points
				source = getInstitutionalBucket();
				dest   = this;
			} else if (record.get("Type").equalsIgnoreCase("Return")) {
				// A reversed sale
				source = Bucket.getExternalBucket();
				dest   = this;
			} else {
				// Fill in based on the sign of the transaction
				if(amt_raw > 0) {
					source = null; // unknown
					dest   = this; // income
				} else {
					source = this; // expense
					dest   = null; // unknown
				}
			}
			Transaction tr = new Transaction(source, dest, record.get("Type").toUpperCase(), record.get("Description"), amt_pos, date);
			transactions.add(tr);
		}
	}

	// Singleton bucket to represent the financial institution
	private static Bucket chase = null;
	public static Bucket getInstitutionalBucket() {
		if(chase == null) {
			chase = new Bucket("Chase Card Services");
		}
		return chase;
	}
}
