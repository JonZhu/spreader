/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;

/**
 * 对象空间, 用于放在变量对象
 * 
 * @author zhujun
 * @date 2015-2-10
 *
 */
public class ObjScope {

	private ObjScope parentScope;
	
	private final Map<String, Object> scopeMap = new HashMap<String, Object>();
	
	public ObjScope(ObjScope parent) {
		this.parentScope = parent;
	}
	
	public void setObj(String name, Object value) {
		scopeMap.put(name, value);
	}
	
	public Object getObj(String name) {
		// 处理全局变量
		if ("ts".equals(name)) {
			return System.currentTimeMillis();
		} else if ("randomChar".equals(name)) {
			return randomChar();
		}
		
		// 本级变量
		if (scopeMap.containsKey(name)) {
			return scopeMap.get(name);
		}
		
		// 父级变量
		return parentScope == null ? null : parentScope.getObj(name);
	}

	/**
	 * @return
	 */
	private String randomChar() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			sb.append((char)(65 + RandomUtils.nextInt(26)));
		}
		return sb.toString();
	}
	
}
