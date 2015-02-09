/**
 * 
 */
package com.juncke.spreader.deliver.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
		
		Iterator<Element> configChildIter = config.elementIterator();
		if (!configChildIter.hasNext()) {
			return IDeliver.RESULT_SUCCESS;
		}
		
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			while (configChildIter.hasNext()) {
				Element childEle = configChildIter.next();
				
				String eleName = childEle.getName();
				if ("var".equals(eleName)) {
					executeVar(childEle);
				} else if ("request".equals(eleName)) {
					executeRequest(client, childEle, content);
				} else if ("sleep".equals(eleName)) {
					executeSleep(childEle);
				} else {
					LOG.warn("http deliver不支持 {} 操作", eleName);
				}
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
	 * 执行Sleep
	 * 
	 * @param sleepEle
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private void executeSleep(Element sleepEle) {
		String sleepMs = sleepEle.getText();
		if (StringUtils.isNumeric(sleepMs)) {
			LOG.debug("Start sleep: {} ms", sleepMs);
			try {
				Thread.sleep(Long.valueOf(sleepMs));
			} catch (Exception e) {
				LOG.error("sleep出错", e);
			}
		}
	}


	/**
	 * 执行http请求
	 * 
	 * @param childEle
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private void executeRequest(CloseableHttpClient client, Element childEle, String content) throws Exception {
		String method = childEle.attributeValue("method", "get");
		HttpRequestBase request = null;
		if ("POST".equalsIgnoreCase(method)) {
			HttpPost httpPost = new HttpPost();
			
			// post param
			List<Element> paramEles = childEle.selectNodes("params/param");
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
		
		request.setURI(new URI(childEle.attributeValue("url")));
		
		// header
		List<Element> headerEles = childEle.selectNodes("headers/header");
		if (headerEles != null && !headerEles.isEmpty()) {
			for (Element headerEle : headerEles) {
				request.addHeader(headerEle.attributeValue("name"), headerEle.getText());
			}
		}
		
		// cookie todo
		
		client.execute(request);
		
	}


	/**
	 * 执行变量初始化
	 * 
	 * @param varEle
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private void executeVar(Element varEle) {
		// TODO Auto-generated method stub
		
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
