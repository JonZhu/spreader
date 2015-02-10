/**
 * 
 */
package com.juncke.spreader.startup;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.juncke.spreader.deliver.IDeliver;
import com.juncke.spreader.spread.Spread;

/**
 * Spread执行Job
 * 
 * @author zhujun
 * @date 2015-2-9
 *
 */
public class SpreadJob implements Job {

	public final static String DATA_SPREAD_KEY = "spread";
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		
		if (dataMap == null) {
			return;
		}
		
		
		Spread spread = (Spread)dataMap.get(DATA_SPREAD_KEY);
		if (spread == null || spread.getDeliverList() == null || spread.getDeliverList().isEmpty()) {
			return;
		}
		
		for (IDeliver deliver : spread.getDeliverList()) {
			deliver.delive(spread.getContent());
		}

	}

}
