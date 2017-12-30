package moneybuckets.buckets.lakesunapee;

import java.util.Date;

import moneybuckets.Bucket;
import moneybuckets.Transaction;

public class LakeSunapeeAcctTransaction extends Transaction {
	public enum TransactionType {
		FEE,
		INTEREST,
		CREDIT,
		DEBIT,
	};
	
	private TransactionType type;
	
	public LakeSunapeeAcctTransaction() {
		setType(null);
	}
	
	public LakeSunapeeAcctTransaction(Bucket src, Bucket dst, String desc, double amt, Date ts, TransactionType t) {
		super(src, dst, desc, amt, ts);
		setType(t);
	}

	public TransactionType getType() { return type; }
	public void setType(TransactionType type) { this.type = type; }
}
