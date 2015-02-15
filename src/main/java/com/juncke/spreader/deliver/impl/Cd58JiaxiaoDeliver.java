/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import org.apache.commons.lang3.RandomUtils;
import org.dom4j.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 成都58驾校信息发布, 使用firefox浏览器模拟
 * 
 * @author zhujun
 * @date 2015-2-15
 *
 */
public class Cd58JiaxiaoDeliver extends AbstractDeliver {

	private final static Logger LOG = LoggerFactory.getLogger(Cd58JiaxiaoDeliver.class);
	
	private Element config;
	
	@Override
	public void config(Element configEle) {
		this.config = configEle;
	}
	
	@Override
	public int delive(String content) {
		
		if (config == null) {
			LOG.error("未指定配置项");
			return RESULT_FAIL;
		}
		
		final ObjScope objScope = new ObjScope(null); // 对象变量
		objScope.setObj("content", content);
		
		WebDriver webDriver = null;
		try {
			String userId = ExpressUtil.eval(config.elementText("userId"), objScope);
			String passwd = config.elementText("password");
			String titleValue = ExpressUtil.eval(config.elementText("title"), objScope);
			String contentValue = config.elementText("content");
			
			String mail = userId + "@163.com";
			
			System.setProperty("webdriver.firefox.bin", config.elementText("firefox"));
			webDriver = new FirefoxDriver();
			JavascriptExecutor jsExecutor = (JavascriptExecutor)webDriver;
			
			// 注册
			webDriver.get("https://passport.58.com/reg/");
			WebElement regForm = webDriver.findElement(By.id("submitForm"));
			regForm.findElement(By.cssSelector("#regTab a")).click();
			regForm.findElement(By.cssSelector("input[name='nickName']")).sendKeys(userId);
			regForm.findElement(By.cssSelector("input[name='txtemail']")).sendKeys(mail);
			regForm.findElement(By.cssSelector("input[name='password']")).sendKeys(passwd);
			regForm.findElement(By.cssSelector("input[name='cpassword']")).sendKeys(passwd);
			regForm.submit();
			
			LOG.info("已经注册58用户: {}, passwd: {}", userId, passwd);
			
			Thread.sleep(4000);
			
			// 发布信息
			webDriver.get("http://post.58.com/102/23087/s5");
			WebElement pForm = webDriver.findElement(By.id("aspnetForm"));
			
			pForm.findElement(By.name("Title")).sendKeys(titleValue);
			
			// 类 别
			pForm.findElement(By.cssSelector("input[value='656331']")).click();
			
			// 驾 照
			pForm.findElement(By.cssSelector("input[value='617390']")).click();
			pForm.findElement(By.cssSelector("input[value='617391']")).click();
			
			// 班 别
			pForm.findElement(By.cssSelector("input[value='617405']")).click();
			
			// 补充说明
			WebElement editor = pForm.findElement(By.cssSelector("div[class=editor]"));
			editor.click();
			editor.sendKeys(contentValue);
			
			// 联系人, 电话
			pForm.findElement(By.name("goblianxiren")).sendKeys("杨先生");
			pForm.findElement(By.name("Phone")).sendKeys("138" + RandomUtils.nextInt(10000000, 99999999));
			
			// 学车地址
			jsExecutor.executeScript("$(\":input[name='localArea']\").val(\"11487\").change().blur();");
			jsExecutor.executeScript("$(\":input[name='localDiduan']\").focus().val(\"11984\").change();");
			pForm.findElement(By.name("dizhi")).sendKeys("驾校驾校驾校");
			
			pForm.submit();
			
			Thread.sleep(2000);

		} catch (Exception e) {
			LOG.error("发布58驾校信息出错", e);
			return RESULT_FAIL;
		} finally {
			if (webDriver != null) {
				webDriver.close();
			}
		}
		
		return RESULT_SUCCESS;
	}

}
