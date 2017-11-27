package moneybuckets;

public class Bucket {
	private boolean isExternal = false;
	private String informalName = "";
	private String institutionName = "";
//	private String accountNumber = "";
	// TODO: balance history
	
	public Bucket() {
		this("");
	}
	
	public Bucket(String informal_name) {
		this(informal_name, false);
	}
	
	public Bucket(String informal_name, boolean is_external) {
		informalName = informal_name;
		isExternal = is_external;
	}
	
}
