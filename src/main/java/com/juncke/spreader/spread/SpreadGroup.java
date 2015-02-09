/**
 * 
 */
package com.juncke.spreader.spread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.juncke.spreader.deliver.IDeliver;
import com.juncke.spreader.deliver.impl.AbstractDeliver;
import com.juncke.spreader.startup.SpreadJob;

/**
 * 
 * @author zhujun
 * @date 2015-2-8
 *
 */
public class SpreadGroup {

	private File config;
	
	private List<Spread> spreadList;
	
	public SpreadGroup(File config) throws DocumentException {
		this.config = config;
		
		parseConfig();
	}

	/**
	 * @throws DocumentException 
	 * 
	 */
	private void parseConfig() throws DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(config);
		Element spreadsEle = doc.getRootElement();
		
		List<Spread> spreads = new ArrayList<>();
		Map<Spread, String> spreadDeliverIds = new HashMap<>(); // spreads -> deliverIds
		Map<String, IDeliver> deliverMap = new HashMap<>();
		
		// 解析
		Iterator<Element> spreadsChildIter = spreadsEle.elementIterator();
		while (spreadsChildIter.hasNext()) {
			Element spreadsChildEle = spreadsChildIter.next();
			String childEleName = spreadsChildEle.getName();
			if ("spread".equals(childEleName)) {
				Spread spread = parseSpread(spreadsChildEle);
				spreads.add(spread);
				spreadDeliverIds.put(spread, spreadsChildEle.elementText("delivers"));
			} else if ("deliver".equals(childEleName)) {
				IDeliver deliver = parseDeliver(spreadsChildEle);
				deliverMap.put(deliver.getId(), deliver);
			}
		}
		
		// 设置spread 的 deliver列表
		for (Entry<Spread, String> entry : spreadDeliverIds.entrySet()) {
			String deliverIds = entry.getValue();
			if (deliverIds != null) {
				String[] deliverIdArr = deliverIds.split(",");
				List<IDeliver> deliverList = new ArrayList<>();
				for (String deliverId : deliverIdArr) {
					IDeliver deliver = deliverMap.get(deliverId);
					if (deliver != null) {
						deliverList.add(deliver);
					}
				}
				entry.getKey().setDeliverList(deliverList);
			}
		}
		
		this.spreadList = spreads;
	}

	/**
	 * 解析Deliver
	 * 
	 * @param deliverEle
	 * @return
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private IDeliver parseDeliver(Element deliverEle) {
		String className = deliverEle.attributeValue("class");
		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("加载内容分发器"+ className +"失败", e);
		}
		
		if (!AbstractDeliver.class.isAssignableFrom(clazz)) {
			throw new RuntimeException("不支持指定内容分发器:" + className);
		}
		
		AbstractDeliver deliver = null;
		try {
			deliver = (AbstractDeliver)clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("实例化内容颁发器"+ className +"出错", e);
		}
		
		deliver.setId(deliverEle.attributeValue("id"));
		deliver.config(deliverEle.element("config"));
		
		return deliver;
	}

	/**
	 * 解析Spread
	 * 
	 * @param spreadEle
	 * @return
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private Spread parseSpread(Element spreadEle) {
		Spread spread = new Spread();
		spread.setCron(spreadEle.attributeValue("cron"));
		spread.setContent(spreadEle.elementText("content"));
		return spread;
	}

	/**
	 * @param scheduler
	 * @throws SchedulerException 
	 */
	public void schedule(Scheduler scheduler) throws SchedulerException {
		if (spreadList == null || spreadList.isEmpty()) {
			return;
		}
		
		for (Spread spread : spreadList) {
			Trigger trigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(spread.getCron())).build();
			
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(SpreadJob.DATA_SPREAD_KEY, spread);
			JobDetail jobDetail = JobBuilder.newJob(SpreadJob.class).usingJobData(jobDataMap).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
			
		}
		
		
	}
	
	
	
}
