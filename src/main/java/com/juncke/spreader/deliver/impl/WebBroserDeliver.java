/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;


/**
 * 模拟
 * 
 * @author zhujun
 * @date 2015-2-12
 *
 */
public class WebBroserDeliver extends AbstractDeliver {

	private final static Logger LOG = LoggerFactory.getLogger(WebBroserDeliver.class);
	
	
	@Override
	public int delive(String content) {
		// TODO Auto-generated method stub
		
		String id = "id32f45tt6248";
		String mail = id + "@qq.com";
		String passwd = "www123mmm";
		
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		try {
			// 注册
			HtmlPage page = webClient.getPage("https://passport.58.com/reg/");
			
			HtmlForm regForm = page.getFormByName("submitForm");
			page.getAnchorByText("邮箱注册").click();
			
			// regForm.setActionAttribute("/doregister");
			
			regForm.getInputByName("nickName").setValueAttribute(id);
			regForm.getInputByName("txtemail").setValueAttribute(mail);
			regForm.getInputByName("password").setValueAttribute(passwd);
			regForm.getInputByName("cpassword").setValueAttribute(passwd);
			
			HtmlPage page2 = ((HtmlSubmitInput)regForm.getInputByValue("立即注册")).click();
			
			System.out.println(page2.asText());
			
			
			// 发布信息
			HtmlPage publishPage = webClient.getPage("http://post.58.com/102/23087/s5");
			HtmlForm pForm = publishPage.getFormByName("aspnetForm");
			pForm.getInputByName("Title").setValueAttribute("驾校照人3000");
			pForm.getInputByValue("656331").click();
			pForm.getInputByValue("617390").click();
			pForm.getInputByValue("617405").click();
			((HtmlElement)publishPage.getFirstByXPath("//div[@class='editor']")).setTextContent("驾校照人3000,驾校照人3000,驾校照人3000,驾校照人3000,驾校照人3000,");
			pForm.getTextAreaByName("Content").setText("驾校照人3000,驾校照人3000,驾校照人3000,驾校照人3000,驾校照人3000,");
			pForm.getInputByName("goblianxiren").setValueAttribute("杨先生");
			pForm.getInputByName("Phone").setValueAttribute("13859873215");
			pForm.getSelectByName("localArea").setDefaultValue("11487");
			pForm.getSelectByName("localDiduan").setDefaultValue("11984");
			pForm.getInputByName("dizhi").setValueAttribute("驾校驾校驾校");
			
			HtmlPage page3 = ((HtmlSubmitInput)publishPage.getElementById("fabu")).click();
			System.out.println(page3.asText());
		} catch (Exception e) {
			LOG.error("error", e);
		}
		
		return 0;
	}
	
	
	public static void main(String[] args) {
		new WebBroserDeliver().delive(null);
	}

}
