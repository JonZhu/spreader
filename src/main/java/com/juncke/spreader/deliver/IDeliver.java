/**
 * 
 */
package com.juncke.spreader.deliver;

/**
 * 内容分发 接口
 * 
 * @author zhujun
 * @date 2015-2-8
 *
 */
public interface IDeliver {

	/**
	 * 成功返回
	 */
	int RESULT_SUCCESS = 0;
	
	/**
	 * 失败 返回
	 */
	int RESULT_FAIL = 1;
	
	/**
	 * 分发内容
	 * @param content
	 * @return
	 */
	int delive(String content);
	
}
