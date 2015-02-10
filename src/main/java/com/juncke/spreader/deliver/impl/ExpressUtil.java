/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 表达式工具
 * 
 * @author zhujun
 * @date 2015-2-10
 *
 */
public class ExpressUtil {

	private final static Logger LOG = LoggerFactory.getLogger(ExpressUtil.class);
	
	/**
	 * 计算表达式
	 * @param express
	 * @param objScope
	 * @return
	 * @author zhujun
	 * @date 2015-2-10
	 */
	public static String eval(String express, ObjScope objScope) {
		
		if (express == null) {
			return null;
		}
		
		
		StringBuilder result = new StringBuilder(); // 整个表达式结果
		int expLength = express.length();
		StringBuilder subExp = null; // 当前子表达式
		char c;
		for (int i = 0; i < expLength; i++) {
			// 搜索${}中的子表达式
			c = express.charAt(i);
			if (subExp == null) {
				// 还未开始进入子表达式
				if ('$' == c && i + 1 < expLength && express.charAt(i + 1) == '{') {
					// 搜索到连续 ${ 字符
					subExp = new StringBuilder(); // 开启子表达式
					i++; // 跳过 { 字符
				} else {
					// 子表达式之外的字符, 原本写入到结果
					result.append(c); 
				}
				
			} else {
				// 已经进入子表达式
				if (c == '}') {
					// 子表达式结束
					result.append(getSubExpValue(subExp.toString(), objScope));
					subExp = null; // 取消开始标识
				} else {
					// 子表达式未到结束
					subExp.append(c);
				}
				
			}
		}
		
		
		return result.toString();
	}

	/**
	 * @param subExp
	 * @param objScope
	 * @return
	 */
	private static String getSubExpValue(String subExp, ObjScope objScope) {
		// 暂时只支持简单变量值
		if (subExp == null || objScope == null) {
			return null;
		}
		
		Object value = objScope.getObj(subExp);
		if (LOG.isDebugEnabled()) {
			LOG.debug("子表达式: {}, value: {}", subExp, value);
		}
		
		return  value == null ? "" : value.toString();
	}
	
}
