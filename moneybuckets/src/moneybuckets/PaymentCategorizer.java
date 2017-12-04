package moneybuckets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class PaymentCategorizer {
	private static class Rule {
		public enum MatchType {
			CONTAINS,
			REGEX,
			EQUALS,
		};
		private MatchType type = MatchType.CONTAINS;
		private String target = "";
		private String category = "";
		
		public Rule(String target, MatchType type, String category) {
			this.target = target;
			this.type = type;
			this.category = category;
		}
		
		public boolean MeetsRule(String query) {
			switch(type) {
			case CONTAINS:
				return query.contains(target);
			case EQUALS:
				return query.equalsIgnoreCase(target);
			case REGEX:
				// TODO
				return false;
			default:
				return false;
			}
		}
		
		public String getCategory() { return category; }
	};
	
	private List<Rule> rules = new LinkedList<>();
	
	public void LoadCategorizationRules(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			Rule.MatchType type = Rule.MatchType.CONTAINS;
			try {
				type = Rule.MatchType.valueOf(record.get("type"));
			} catch (IllegalArgumentException e) {
				// do nothing
			}
			Rule r = new Rule(record.get("target"), type, record.get("category"));
			rules.add(r);
		}
	}

	public void CategorizeTransactions(List<Transaction> transactions) {
		for (Transaction tr : transactions) {
			for (Rule r : rules) {
				if(r.MeetsRule(tr.getDescription())) {
					tr.setCategory(r.getCategory());
//					System.out.println(tr.getDescription() + " matched rule " + r.getCategory());
					break; // Don't process additional rules
				}
			}
		}
	}
	
	public HashMap<String, Double> GetOutboundTotalsForCategories(List<Transaction> transactions) {
		HashMap<String, Double> totalForCat = new HashMap<String, Double>();
//		System.out.println(transactions);
		final String defaultCat = "unknown";
		for (Transaction tr : transactions) {
			String cat = tr.getCategory();
			if(cat == null) { cat = defaultCat; }
			if(totalForCat.containsKey(cat)) {
				// Not the first transaction
				totalForCat.put(cat, totalForCat.get(cat) + tr.getAmount());
			} else {
				// Very first transaction
				totalForCat.put(cat, tr.getAmount());
			}
		}
		return totalForCat;
	}
}
