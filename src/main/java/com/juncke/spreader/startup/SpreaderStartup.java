/**
 * 
 */
package com.juncke.spreader.startup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dom4j.DocumentException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juncke.spreader.spread.SpreadGroup;

/**
 * 启动入口
 * 
 * @author zhujun
 * @date 2015-2-8
 *
 */
public class SpreaderStartup {

	private final static Logger LOG = LoggerFactory.getLogger(SpreaderStartup.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Options options = new Options();
		options.addOption("h", false, "打印帮助");
		options.addOption("c", true, "配置文件, 使用','分隔");
		options.addOption("cDir", true, "配置文件目录, 使用','分隔");
		
		CommandLine cli = null;
		try {
			cli = new BasicParser().parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		List<File> spreadsList = new ArrayList<>();
		String configDir = cli.getOptionValue("cDir");
		if (configDir != null) {
			spreadsList.addAll(getSpreadsFileFromDir(configDir));
		}
		
		String configFile = cli.getOptionValue("c");
		if (configFile != null) {
			spreadsList.addAll(getSpreadsFileFromFiles(configFile));
		}
		
		if (spreadsList.isEmpty()) {
			System.out.println("No spreads config files");
			System.exit(0);
		}
		
		Scheduler scheduler = null;
		try {
			scheduler = getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			System.out.println("Create scheduler error");
			e.printStackTrace();
			System.exit(1);
		}
		
		for (File spreadsFile : spreadsList) {
			startUpSpreads(scheduler, spreadsFile);
		}

	}

	/**
	 * @param configFile
	 * @return
	 */
	private static Collection<? extends File> getSpreadsFileFromFiles(
			String configFile) {
		List<File> files = new ArrayList<>();
		String[] fileArray = configFile.split(",");
		for (String fileName : fileArray) {
			File file = new File(fileName);
			if (file.exists() && file.getName().endsWith(".xml")) {
				files.add(file);
			}
		}
		
		return files;
	}

	/**
	 * @param configDir
	 * @return
	 */
	private static Collection<? extends File> getSpreadsFileFromDir(
			String configDir) {
		List<File> files = new ArrayList<>();
		
		String[] dirArray = configDir.split(",");
		for (String dirPath : dirArray) {
			File dir = new File(dirPath);
			if (dir.isDirectory()) {
				File[] dirChildren = dir.listFiles();
				for (File dirChild : dirChildren) {
					if (dirChild.getName().endsWith(".xml")) {
						files.add(dirChild);
					}
				}
			}
		}
		
		return files;
	}

	/**
	 * @return
	 * @throws SchedulerException 
	 */
	private static Scheduler getScheduler() throws SchedulerException {
		StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
		return stdSchedulerFactory.getScheduler();
	}

	/**
	 * 启动Spreads
	 * 
	 * @param scheduler
	 * @param spreadsFile
	 */
	private static void startUpSpreads(Scheduler scheduler, File spreadsFile) {
		SpreadGroup spreadGroup = null;
		try {
			spreadGroup = new SpreadGroup(spreadsFile);
		} catch (DocumentException e) {
			LOG.error("初始化SpreadGroup出错", e);
			return;
		}
		
		try {
			spreadGroup.schedule(scheduler);
		} catch (SchedulerException e) {
			LOG.error("Schedule出错", e);
		}
	}

}
