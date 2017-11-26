package moneybuckets;

import java.util.Date;
import java.util.List;

public class Transaction {
	private Bucket sourceBucket, destBucket;
	private double amount;
	private Date timestamp;
	private List<String> tags;
}
