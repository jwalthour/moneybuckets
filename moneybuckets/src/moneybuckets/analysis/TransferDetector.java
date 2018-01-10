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
		
		public boolean isTransferSource(Transaction tr) {
			boolean transTypeMatches = stringMatches(typeCompareType, tr.getType(), descString);
			boolean descMatches = stringMatches(descCompareType, tr.getDescription(), descString);
			boolean bucketSourceMatches = tr.getSourceBucket().getClass().getName().equalsIgnoreCase(bucketTypeFoundIn);
			return transTypeMatches && descMatches && bucketSourceMatches;
		}
		
		private boolean stringMatches(MatchType type, String query, String target) {
			switch (typeCompareType) {
			case ANY:
				return true;
			case CONTAINS:
				return query.toUpperCase().contains(target);
			case EQUALS:
				return query.equalsIgnoreCase(target);
			case REGEX:
				// TODO
				return false;
			default:
				return false;
			}
		}
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
	public List<Transaction> detectTransfers(List<Transaction> transactions1, List<Transaction> transactions2) {
		List<Transaction> transfers = new LinkedList<>();
		// Look for one end
		for (Transaction tr1 : transactions1) {
			for (Rule r : rules) {
				if(r.isTransferSource(tr1)) {
					transactions1.remove(tr1);
					// Look for the other
					for (Transaction tr2 : transactions2) {
						
					}
					
					transfers.add(tr1);
				}
			}
		}
		
		return transfers;
	}
	
}
