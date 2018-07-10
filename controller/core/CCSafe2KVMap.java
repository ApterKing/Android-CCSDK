package cc.sdkutil.controller.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangcong on 14-12-20. <br>
 * 双键值对ConcurrentHashMap. <br>
 * @param <K1>
 * @param <K2>
 * @param <V>
 */
public class CCSafe2KVMap<K1, K2, V> {

	private ConcurrentHashMap<K1, ConcurrentHashMap<K2, V>> kkVMap;

	public CCSafe2KVMap() {
		this.kkVMap = new ConcurrentHashMap<K1, ConcurrentHashMap<K2, V>>();
	}

	public void put(K1 key1, K2 key2, V value) {
		if (kkVMap.containsKey(key1)) {
			ConcurrentHashMap<K2, V> k2V_map = kkVMap.get(key1);
			k2V_map.putIfAbsent(key2, value);
		} else {
			ConcurrentHashMap<K2, V> k2V_map = new ConcurrentHashMap<K2, V>();
			k2V_map.put(key2, value);
			kkVMap.putIfAbsent(key1, k2V_map);
		}
	}

	public Set<K1> getFirstKeys() {
		return kkVMap.keySet();
	}

	public ConcurrentHashMap<K2, V> get(K1 key1) {
		return kkVMap.get(key1);
	}

	public V get(K1 key1, K2 key2) {
		ConcurrentHashMap<K2, V> k2_v = kkVMap.get(key1);
		return k2_v == null ? null : k2_v.get(key2);
	}

	public boolean containsKey(K1 key1, K2 key2) {
		if (kkVMap.containsKey(key1)) {
			return kkVMap.get(key1).containsKey(key2);
		}
		return false;
	}

	public boolean containsKey(K1 key1) {
		return kkVMap.containsKey(key1);
	}

	public int size() {
		if (kkVMap.size() == 0)
			return 0;

		int result = 0;
		for (ConcurrentHashMap<K2, V> k2V_map : kkVMap.values()) {
			result += k2V_map.size();
		}
		return result;
	}

	public void clear() {
		if (kkVMap.size() > 0) {
			for (ConcurrentHashMap<K2, V> k2V_map : kkVMap.values()) {
				k2V_map.clear();
			}
			kkVMap.clear();
		}
	}
}
