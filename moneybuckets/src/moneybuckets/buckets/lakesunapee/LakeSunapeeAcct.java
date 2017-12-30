package moneybuckets.buckets.lakesunapee;

import moneybuckets.Bucket;

public class LakeSunapeeAcct extends Bucket {

	public LakeSunapeeAcct() {
		super("Lake Sunapee account", false);
	}

	public LakeSunapeeAcct(String informal_name) {
		super("Lake Sunapee " + informal_name, false);
	}
}
