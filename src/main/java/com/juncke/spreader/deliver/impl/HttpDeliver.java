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
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
		
		final ObjScope objScope = new ObjScope(null); // 对象变量
		objScope.setObj("content", content);
		
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			while (configChildIter.hasNext()) {
				Element childEle = configChildIter.next();
				
				String eleName = childEle.getName();
				if ("var".equals(eleName)) {
					executeVar(objScope, childEle);
				} else if ("request".equals(eleName)) {
					executeRequest(objScope, client, childEle);
				} else if ("sleep".equals(eleName)) {
					executeSleep(childEle);
				} else if ("log".equals(eleName)) {
					executeLog(objScope, childEle);
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
	 * @param objScope
	 * @param logEle
	 * @author zhujun
	 * @date 2015-2-11
	 */
	private void executeLog(ObjScope objScope, Element logEle) {
		String message = logEle.attributeValue("message");
		if (StringUtils.isEmpty(message)) {
			return;
		}
		
		List<Element> paramEles = logEle.elements("param");
		Object[] logParams = null;
		if (paramEles != null && !paramEles.isEmpty()) {
			List<Object> paramList = new ArrayList<>();
			for (Element paramEle : paramEles) {
				paramList.add(ExpressUtil.eval(paramEle.getText(), objScope));
			}
			
			logParams = paramList.toArray();
		}
		
		LOG.info(message, logParams);
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
	 * @param requestEle
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private void executeRequest(final ObjScope objScope, CloseableHttpClient client, Element requestEle) throws Exception {
		String reqId = requestEle.attributeValue("id");
		String method = requestEle.attributeValue("method", "get");
		HttpRequestBase request = null;
		if ("POST".equalsIgnoreCase(method)) {
			HttpPost httpPost = new HttpPost();
			
			// post param
			List<Element> paramEles = requestEle.selectNodes("params/param");
			if (paramEles != null && !paramEles.isEmpty()) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (Element paramEle : paramEles) {
					paramList.add(new BasicNameValuePair(paramEle.attributeValue("name"), ExpressUtil.eval(paramEle.getText(), objScope)));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(paramList, Consts.UTF_8));
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
		
		
		CloseableHttpResponse response = client.execute(request);
		LOG.debug("http response status: {}", response.getStatusLine());
		
		if (StringUtils.isNotEmpty(reqId)) {
			// save response
			objScope.setObj(reqId + "_response", EntityUtils.toString(response.getEntity()));
		}
		IOUtils.closeQuietly(response);
		
	}


	/**
	 * 执行变量初始化
	 * 
	 * @param varEle
	 * @author zhujun
	 * @date 2015-2-9
	 */
	private void executeVar(final ObjScope objScope, Element varEle) {
		String express = varEle.getText();
		if (StringUtils.isNotEmpty(express)) {
			
			String name = varEle.attributeValue("name");
			String value = ExpressUtil.eval(express, objScope);
			objScope.setObj(name, value);
		}
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
