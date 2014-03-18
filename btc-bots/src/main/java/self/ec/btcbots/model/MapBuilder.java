package self.ec.btcbots.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

	private final Map<Object,Object> map = new HashMap<>();
	
	public MapBuilder entry(Object key, Object value) {
		map.put(key, value);
		return this;
	}
	
	public Map<Object,Object> build() {
		return Collections.unmodifiableMap(map);
	}
}
