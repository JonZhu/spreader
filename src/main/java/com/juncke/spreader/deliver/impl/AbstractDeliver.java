/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import org.dom4j.Element;

import com.juncke.spreader.deliver.IDeliver;

/**
 * @author zhujun
 * @date 2015-2-9
 *
 */
public abstract class AbstractDeliver implements IDeliver {

	private String id;
	
	@Override
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 配置
	 * @param configEle
	 * @author zhujun
	 * @date 2015-2-9
	 */
	public void config(Element configEle) {
		
	}

}
