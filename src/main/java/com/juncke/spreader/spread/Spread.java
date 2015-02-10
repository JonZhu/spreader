/**
 * 
 */
package com.juncke.spreader.spread;

import java.util.List;

import com.juncke.spreader.deliver.IDeliver;

/**
 * @author zhujun
 * @date 2015-2-8
 *
 */
public class Spread {

	private String cron;
	
	private String content;
	
	/**
	 * 内容分发器列表
	 */
	private List<IDeliver> deliverList;

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<IDeliver> getDeliverList() {
		return deliverList;
	}

	public void setDeliverList(List<IDeliver> deliverList) {
		this.deliverList = deliverList;
	}
	
	
	
}
