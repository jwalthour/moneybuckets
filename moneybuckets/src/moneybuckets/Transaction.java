package moneybuckets;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Transaction {
	private Bucket sourceBucket, destBucket;
	private double amount;
	private Date timestamp;
	private String type; // The type from the statement.  For example, a credit card may list the transaction type as "SALE", "FEE", or "PAYMENT".
	private String description; // The description from the statement
	private String category; // The category assigned in MoneyBuckets
	private List<String> tags = new LinkedList<>();
	
	public static final String UNCATEGORIZED = "Uncategorized"; 
	
	public Transaction() {
		this(null, null, "", "", 0, null);
	}
	
	public Transaction(Bucket src, Bucket dst, String type, String desc, double amt, Date ts) {
		sourceBucket = src;
		destBucket = dst;
		setType(type);
		setDescription(desc);
		amount = amt;
		timestamp = ts;
		category = UNCATEGORIZED;
	}
	
	@Override
	public String toString() {
		return "{ type: \"" + getType() + "\", description: \"" + getDescription() + "\", amount: " + getAmount() + ", category: \"" + getCategory() + "\"}";
	}
	
	public boolean dateAndValueMatch(Transaction other) {
		if(getAmount() == other.getAmount()) {
			long diffInMillis = Math.abs(getTimestamp().getTime() - other.getTimestamp().getTime());
			TimeUnit d = TimeUnit.HOURS;
			return (d.convert(diffInMillis, TimeUnit.MILLISECONDS) <= 24);
		} else {
			return false;
		}
	}
	
	// Trivial getters and setters
	public Bucket getSourceBucket() { return sourceBucket; 	}
	public void setSourceBucket(Bucket sourceBucket) { this.sourceBucket = sourceBucket; }
	public Bucket getDestBucket() { return destBucket; }
	public void setDestBucket(Bucket destBucket) { this.destBucket = destBucket; }
	public double getAmount() { return amount; }
	public void setAmount(double amount) { this.amount = amount; }
	public Date getTimestamp() { return timestamp; }
	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public void addTag(String tag) { tags.add(tag); }
	public boolean hasTag(String tag) { return tags.contains(tag); }
	
	public static class ComparatorUncatCatDescAmount implements Comparator<Transaction> {
		@Override
		public int compare(Transaction o1, Transaction o2) {
			// First sort criteria: Uncategorized first
			if        ( o1.getCategory().equals(UNCATEGORIZED) && !o2.getCategory().equals(UNCATEGORIZED)) {
				return -1;
			} else if (!o1.getCategory().equals(UNCATEGORIZED) &&  o2.getCategory().equals(UNCATEGORIZED)) {
				return 1;
			} else {
				// Second sort criteria: category name
				if(!o1.getCategory().equals(o2.getCategory())) {
					return o1.getCategory().compareTo(o2.getCategory());
				} else {
					// Third sort criteria: transaction amount
					return ((Double)o1.getAmount()).compareTo(o2.getAmount());
				}
			}
		}
	}
	public static class ComparatorUncatCatByTotalDescAmount implements Comparator<Transaction> {
		private HashMap<String, Double> catTotals;
		public ComparatorUncatCatByTotalDescAmount(HashMap<String, Double> catTotals) {
			this.catTotals = catTotals;
		}
		@Override
		public int compare(Transaction o1, Transaction o2) {
			// First sort criteria: Uncategorized first
			if        ( o1.getCategory().equals(UNCATEGORIZED) && !o2.getCategory().equals(UNCATEGORIZED)) {
				return -1;
			} else if (!o1.getCategory().equals(UNCATEGORIZED) &&  o2.getCategory().equals(UNCATEGORIZED)) {
				return 1;
			} else {
				// Second sort criteria: category amount
				if(!o1.getCategory().equals(o2.getCategory()) && !o1.getCategory().equals(UNCATEGORIZED)
						&& catTotals.containsKey(o1.getCategory()) && catTotals.containsKey(o2.getCategory())) {
					return ((Double)catTotals.get(o1.getCategory())).compareTo(catTotals.get(o2.getCategory()));
				} else {
					// Third sort criteria: transaction amount
					return ((Double)o1.getAmount()).compareTo(o2.getAmount());
				}
			}
		}
	}

}
