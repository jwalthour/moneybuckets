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

public class ChaseCreditCardTransactionCategorizer {
	private static class Rule {
		private String transType;
		public enum MatchType {
			CONTAINS,
			REGEX,
			EQUALS,
		};
		private MatchType type = MatchType.CONTAINS;
		private String target = "";
		private String category = "";
		
		public Rule(String transType, String target, MatchType type, String category) {
			this.transType = transType;
			this.target = target;
			this.type = type;
			this.category = category;
		}
		
		public boolean MeetsRule(String tt, String query) {
			if(tt.equalsIgnoreCase(transType)) {
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

	public ChaseCreditCardTransactionCategorizer() {
		
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
			Rule r = new Rule(record.get("transType"), record.get("target"), type, record.get("category"));
			rules.add(r);
		}
	}

	/**
	 * 
	 * @param transactions - MUST be a list of ChaseCreditCardTransactions
	 */
	public void categorizeTransactions(List<Transaction> transactions) {
		for (Transaction tr : transactions) {
			for (Rule r : rules) {
				if(r.MeetsRule(tr.getType(), tr.getDescription())) {
					tr.setCategory(r.getCategory());
//					System.out.println(tr.getDescription() + " matched rule " + r.getCategory());
					break; // Don't process additional rules
				}
			}
		}
	}
}
