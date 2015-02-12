/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * 模拟
 * 
 * @author zhujun
 * @date 2015-2-12
 *
 */
public class WebBroserDeliver extends AbstractDeliver {

	@Override
	public int delive(String content) {
		// TODO Auto-generated method stub
		System.setProperty("webdriver.firefox.bin", "E:/Program Files/Mozilla Firefox/firefox.exe");
		
		WebDriver webDriver = new HtmlUnitDriver(true);
		webDriver.get("http://www.baidu.com");
		
		return 0;
	}
	
	
	public static void main(String[] args) {
		new WebBroserDeliver().delive(null);
	}

}
