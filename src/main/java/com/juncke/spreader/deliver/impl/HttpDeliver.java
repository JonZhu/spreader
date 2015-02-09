/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juncke.spreader.deliver.IDeliver;

/**
 * Http内容分发
 * 
 * @author zhujun
 * @date 2015-2-8
 *
 */
public class HttpDeliver extends AbstractDeliver implements IDeliver {

	private final static Logger LOG = LoggerFactory.getLogger(HttpDeliver.class);
	
	private Element config;
	
	@Override
	public void config(Element configEle) {
		this.config = configEle;
	}
	
	
	@Override
	public int delive(String content) {
		if (config == null) {
			return IDeliver.RESULT_SUCCESS;
		}
		
		List<Element> requestEles = config.elements("request");
		if (requestEles.isEmpty()) {
			return IDeliver.RESULT_SUCCESS;
		}
		
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			for (Element requestEle : requestEles) {
				String method = requestEle.attributeValue("method", "get");
				HttpRequestBase request = null;
				if ("POST".equalsIgnoreCase(method)) {
					HttpPost httpPost = new HttpPost();
					
					// post param
					List<Element> paramEles = requestEle.selectNodes("params/param");
					if (paramEles != null && !paramEles.isEmpty()) {
						List<NameValuePair> paramList = new ArrayList<NameValuePair>();
						for (Element paramEle : paramEles) {
							paramList.add(new BasicNameValuePair(paramEle.attributeValue("name"), replcaceContent(paramEle.getText(), content)));
						}
						httpPost.setEntity(new UrlEncodedFormEntity(paramList));
					}
					
					request = httpPost;
				} else {
					request = new HttpGet();
				}
				
				request.setURI(new URI(requestEle.attributeValue("url")));
				
				// header
				List<Element> headerEles = requestEle.selectNodes("headers/header");
				if (headerEles != null && !headerEles.isEmpty()) {
					for (Element headerEle : headerEles) {
						request.addHeader(headerEle.attributeValue("name"), headerEle.getText());
					}
				}
				
				// cookie todo
				
				client.execute(request);
			}
		} catch (Exception e) {
			LOG.error("Deliver执行出错", e);
			return IDeliver.RESULT_FAIL;
		} finally {
			IOUtils.closeQuietly(client);
		}
		
		return IDeliver.RESULT_SUCCESS;
	}


	/**
	 * 替换text中的${content}为 content
	 * 
	 * @param text
	 * @param content
	 * @return
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private String replcaceContent(String text, String content) {
		if (text == null) {
			return null;
		}
		
		return text.replaceAll("\\$\\{\\s*content\\s*\\}", content);
	}


}
