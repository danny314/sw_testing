package pset3;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueDomains {
	private Map<String, List<Object>> valuemap;

	public ValueDomains() {
		valuemap = new HashMap<String, List<Object>>();
		List<Object> booleans = new ArrayList<Object>();
		booleans.add(true);
		booleans.add(false);
		valuemap.put("boolean", booleans);
		List<Object> ints = new ArrayList<Object>();
		ints.add(-7);
		ints.add(0);
		ints.add(4);
		valuemap.put("int", ints);
		List<Object> strings = new ArrayList<Object>();
		strings.add(null);
		strings.add("\"\"");
		strings.add("\"-31\"");
		strings.add("\"hello there\"");
		valuemap.put("String", strings);
	}

	public void setDomain(String type, List<Object> domain) {
		valuemap.put(type, domain);
	}

	public void addValue(String type, Object val) {
		if (valuemap.containsKey(type)) {
			valuemap.get(type).add(val);
		} else {
			List<Object> set = new ArrayList<Object>();
			set.add(val);
			valuemap.put(type, set);
		}
	}

	public List<Object> getDomain(String type) {
		return valuemap.get(type);
	}
}
