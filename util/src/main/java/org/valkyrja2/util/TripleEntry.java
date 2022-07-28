/*
 * PROJECT valkyrja2
 * util/TripleEntry.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.util.Map;

/**
 * 三元素的Entry对象
 *
 * @author Tequila
 * @create 2022/06/30 22:33
 **/
public class TripleEntry<K, V1, V2> {

	/** null标记 */
	private static final String NULL = "<null>";

	/** key */
	private K key;

	/** v1 */
	private V1 v1;

	/** v2 */
	private V2 v2;
	
	public TripleEntry(K key, V1 v1) {
		super();
		
		this.key = key;
		this.v1 = v1;
	}
	
	public TripleEntry(K key, V1 v1, V2 v2) {
		super();
		
		this.key = key;
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V1 getV1() {
		return v1;
	}

	public void setV1(V1 v1) {
		this.v1 = v1;
	}

	public V2 getV2() {
		return v2;
	}

	public void setV2(V2 v2) {
		this.v2 = v2;
	}
	
	public String toString() {
		return String.format("key: %s, V1: %s, V2: %s", 
				key != null ? key.toString() : NULL,
				v1 != null ? v1.toString() : NULL,
				v2 != null ? v2.toString() : NULL);
	}
	
}
