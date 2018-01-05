package moneybuckets;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Bucket {
	private boolean isExternal = false;
	private String informalName = "";
	private String institutionName = "";
	protected List<Transaction> transactions = new LinkedList<>();
//	private String accountNumber = "";
	// TODO: balance history
	
	public Bucket() {
		this("");
	}
	
	public Bucket(String informal_name) {
		this(informal_name, false);
	}
	
	public Bucket(String informal_name, boolean is_external) {
		informalName = informal_name;
		isExternal = is_external;
	}
	

	public List<Transaction> getTransactions() {
		return transactions;
	}
		
	public List<Transaction> getExpenses() {
		List<Transaction> expenses = new LinkedList<>();
		for (Transaction tr : transactions) {
			if(tr.getDestBucket() == Bucket.getExternalBucket()) {
				expenses.add(tr);
			}
		}
		return expenses;
	}

	public List<Transaction> getExpenses(Date timeRangeStart, Date timeRangeEnd) {
		List<Transaction> expenses = new LinkedList<>();
		for (Transaction tr : transactions) {
			if(tr.getSourceBucket() == this) {
				if(tr.getTimestamp().after(timeRangeStart) && tr.getTimestamp().before(timeRangeEnd)) {
					expenses.add(tr);
				}
			}
		}
		return expenses;
	}

	public List<Transaction> getIncomes(Date timeRangeStart, Date timeRangeEnd) {
		List<Transaction> expenses = new LinkedList<>();
		for (Transaction tr : transactions) {
			if(tr.getDestBucket() == this) {
				if(tr.getTimestamp().after(timeRangeStart) && tr.getTimestamp().before(timeRangeEnd)) {
					expenses.add(tr);
				}
			}
		}
		return expenses;
	}

	// Singleton one to represent general external entities
	private static Bucket external = null;
	public static Bucket getExternalBucket() {
		if(external == null) {
			external = new Bucket("External");
		}
		return external;
	}
}
