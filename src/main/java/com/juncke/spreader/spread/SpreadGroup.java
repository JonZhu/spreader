/**
 * 
 */
package com.juncke.spreader.spread;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.quartz.Scheduler;

/**
 * 
 * @author zhujun
 * @date 2015-2-8
 *
 */
public class SpreadGroup {

	private File config;
	
	private List<Spread> spreadList;
	
	public SpreadGroup(File config) {
		this.config = config;
		
		parseConfig();
	}

	/**
	 * 
	 */
	private void parseConfig() {
		// TODO Auto-generated method stub
		
		// Document doc = DocumentHelper.p
		
	}

	/**
	 * @param scheduler
	 */
	public void schedule(Scheduler scheduler) {
		
		
	}
	
	
	
}
