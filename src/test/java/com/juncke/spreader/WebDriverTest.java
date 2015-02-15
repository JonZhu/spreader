/**
 * 
 */
package com.juncke.spreader;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author zhujun
 * @date 2015-2-15
 *
 */
public class WebDriverTest {

	@Test
	public void test() {
		
		String id = "id482kmzdsk6564te8";
		String mail = id + "@163.com";
		String passwd = "www123ppp";
		
		
		System.setProperty("webdriver.firefox.bin", "F:\\Program Files\\Mozilla Firefox\\firefox.exe");
		WebDriver webDriver = new FirefoxDriver();
		JavascriptExecutor jsExecutor = (JavascriptExecutor)webDriver;
		
		try {
			// 注册
			webDriver.get("https://passport.58.com/reg/");
			WebElement regForm = webDriver.findElement(By.id("submitForm"));
			regForm.findElement(By.cssSelector("#regTab a")).click();
			regForm.findElement(By.cssSelector("input[name='nickName']")).sendKeys(id);
			regForm.findElement(By.cssSelector("input[name='txtemail']")).sendKeys(mail);
			regForm.findElement(By.cssSelector("input[name='password']")).sendKeys(passwd);
			regForm.findElement(By.cssSelector("input[name='cpassword']")).sendKeys(passwd);
			regForm.submit();
			
			Thread.sleep(4000);
			
			// 发布信息
			webDriver.get("http://post.58.com/102/23087/s5");
			WebElement pForm = webDriver.findElement(By.id("aspnetForm"));
			pForm.findElement(By.name("Title")).sendKeys("驾校收人3000");
			
			pForm.findElement(By.cssSelector("input[value='656331']")).click();
			
			pForm.findElement(By.cssSelector("input[value='617390']")).click();
			pForm.findElement(By.cssSelector("input[value='617405']")).click();
			
			WebElement editor = pForm.findElement(By.cssSelector("div[class=editor]"));
			editor.click();
			editor.sendKeys("驾校照人3000,驾校照人3000,驾校照人3000,驾校照人3000,驾校照人3000");
			
			pForm.findElement(By.name("goblianxiren")).sendKeys("杨先生");
			pForm.findElement(By.name("Phone")).sendKeys("13859873215");
			
			// 学车地址
			jsExecutor.executeScript("$(\":input[name='localArea']\").val(\"11487\").change().blur();");
			jsExecutor.executeScript("$(\":input[name='localDiduan']\").focus().val(\"11984\").change();");
			pForm.findElement(By.name("dizhi")).sendKeys("驾校驾校驾校");
			
			pForm.submit();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println();
		
	}

}
