package moneybuckets.analysis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import moneybuckets.Transaction;

public class TransferDetector {
	private static class Rule {
		public enum MatchType {
			CONTAINS,
			REGEX,
			ANY,
			EQUALS,
		};
		
		public static MatchType mtFromString(String typeString) {
			Rule.MatchType type = Rule.MatchType.CONTAINS;
			try {
				type = Rule.MatchType.valueOf(typeString);
			} catch (IllegalArgumentException e) {
				// do nothing
			}
			return type;
		}
		
		//descString,descCompareType,typeString,typeCompareType,bucketTypeFoundIn,sourceBucketType,destBucketType
		private String    descString        = "";
		private MatchType descCompareType   = MatchType.CONTAINS;
		private String    typeString        = "";
		private MatchType typeCompareType   = MatchType.ANY;
		private String    bucketTypeFoundIn = "";
		private String    sourceBucketType  = "";
		private String    destBucketType    = "";
		
		
		
		public Rule(String descString, MatchType descCompareType, String typeString, MatchType typeCompareType,
				String bucketTypeFoundIn, String sourceBucketType, String destBucketType) {
			this.descString        = descString       ;
			this.descCompareType   = descCompareType  ;
			this.typeString        = typeString       ;
			this.typeCompareType   = typeCompareType  ;
			this.bucketTypeFoundIn = bucketTypeFoundIn;
			this.sourceBucketType  = sourceBucketType ;
			this.destBucketType    = destBucketType   ;
		}
		
//		public boolean MeetsRule() {
//			
////			switch(type) {
////			case CONTAINS:
////				return query.toUpperCase().contains(target.toUpperCase());
////			case EQUALS:
////				return query.equalsIgnoreCase(target);
////			case ANY:
////				return true;
////			case REGEX:
////				// TODO
////				return false;
////			default:
////				return false;
////			}
//			return false;
//		}
		
//		public String getCategory() { return category; }
	};
	
	private List<Rule> rules = new LinkedList<>();

	public void loadRules(String path) throws FileNotFoundException, IOException {
		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			//descString,descCompareType,typeString,typeCompareType,bucketTypeFoundIn,sourceBucketType,destBucketType
			Rule r = new Rule(record.get("descString"), Rule.mtFromString(record.get("descCompareType")), 
					record.get("typeString"), Rule.mtFromString(record.get("typeCompareType")), 
					record.get("bucketTypeFoundIn"), record.get("sourceBucketType"), record.get("destBucketType"));
			rules.add(r);
		}
	}

	// Removes transactions from input lists.  Returns the removed transactions.
	public void detectTransfers(List<Transaction> transactions1, List<Transaction> transactions2) {
		for (Transaction tr : transactions1) {
			for (Rule r : rules) {
//				if(r.MeetsRule(tr.getType(), tr.getDescription())) {
//					tr.setCategory(r.getCategory());
////					System.out.println(tr.getDescription() + " matched rule " + r.getCategory());
//					break; // Don't process additional rules
//				}
			}
		}
	}
	
}
