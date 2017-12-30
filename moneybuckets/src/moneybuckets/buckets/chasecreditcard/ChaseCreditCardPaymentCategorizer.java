package moneybuckets.buckets.chasecreditcard;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.*;

import moneybuckets.Transaction;
import moneybuckets.buckets.chasecreditcard.ChaseCreditCardTransaction.*;

public class ChaseCreditCardPaymentCategorizer /*extends PaymentCategorizer*/ {
	private static class Rule {
		private ChaseCreditCardTransaction.TransactionType transType;
		public enum MatchType {
			CONTAINS,
			REGEX,
			EQUALS,
		};
		private MatchType type = MatchType.CONTAINS;
		private String target = "";
		private String category = "";
		
		public Rule(ChaseCreditCardTransaction.TransactionType transType, String target, MatchType type, String category) {
			this.transType = transType;
			this.target = target;
			this.type = type;
			this.category = category;
		}
		
		public boolean MeetsRule(ChaseCreditCardTransaction.TransactionType tt, String query) {
			if(tt == transType) {
				switch(type) {
				case CONTAINS:
					return query.toUpperCase().contains(target.toUpperCase());
				case EQUALS:
					return query.equalsIgnoreCase(target);
				case REGEX:
					// TODO
					return false;
				default:
					return false;
				}
			} else {
				return false;
			}
		}
		
		public String getCategory() { return category; }
	};
	private List<Rule> rules = new LinkedList<>();

	public ChaseCreditCardPaymentCategorizer() {
		
	}

	public void loadRules(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			Rule.MatchType type = Rule.MatchType.CONTAINS;
			try {
				type = Rule.MatchType.valueOf(record.get("type"));
			} catch (IllegalArgumentException e) {
				// do nothing
			}
			ChaseCreditCardTransaction.TransactionType transType = TransactionType.SALE;
			try {
				transType = ChaseCreditCardTransaction.TransactionType.valueOf(record.get("transType"));
			} catch (IllegalArgumentException e) {
				// do nothing
			}
			Rule r = new Rule(transType, record.get("target"), type, record.get("category"));
			rules.add(r);
		}
	}

	/**
	 * 
	 * @param transactions - MUST be a list of ChaseCreditCardTransactions
	 */
	public void categorizeTransactions(List<Transaction> transactions) {
		for (Transaction tr : transactions) {
			ChaseCreditCardTransaction ctr = (ChaseCreditCardTransaction)tr;
			for (Rule r : rules) {
				if(r.MeetsRule(ctr.getType(), ctr.getDescription())) {
					ctr.setCategory(r.getCategory());
//					System.out.println(tr.getDescription() + " matched rule " + r.getCategory());
					break; // Don't process additional rules
				}
			}
		}
	}
}
