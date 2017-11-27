package moneybuckets;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Transaction {
	private Bucket sourceBucket, destBucket;
	private double amount;
	private Date timestamp;
	private String description;
	private String category;
	private List<String> tags = new LinkedList<>();
	
	public Transaction() {
		this(null, null, "", 0, null);
	}
	
	public Transaction(Bucket src, Bucket dst, String desc, double amt, Date ts) {
		sourceBucket = src;
		destBucket = dst;
		setDescription(desc);
		amount = amt;
		timestamp = ts;
		category = "";
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
	
	public void addTag(String tag) { tags.add(tag); }
	public boolean hasTag(String tag) { return tags.contains(tag); }

}
