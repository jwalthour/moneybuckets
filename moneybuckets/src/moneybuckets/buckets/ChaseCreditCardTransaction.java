package moneybuckets.buckets;

import java.util.Date;

import moneybuckets.Bucket;
import moneybuckets.Transaction;

public class ChaseCreditCardTransaction extends Transaction {
	public enum TransactionType {
		FEE,
		SALE,
		PAYMENT
	};
	
	private TransactionType type;
	
	public ChaseCreditCardTransaction() {
		setType(null);
	}

	public ChaseCreditCardTransaction(Bucket src, Bucket dst, String desc, double amt, Date ts, TransactionType t) {
		super(src, dst, desc, amt, ts);
		setType(t);
	}

	public TransactionType getType() { return type; }
	public void setType(TransactionType type) { this.type = type; }

}
