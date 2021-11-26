package com.nullfish.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SortedHashMap extends HashMap {
	public List getKeyList() {
		List list = new ArrayList(super.keySet());
		Collections.sort(list);
		return list;
	}
}
