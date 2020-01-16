package org.tj.rpc.core;

import java.util.Map;

/**
 * rpc服务映射容器
 * @ClassName: MessageKeyVal
 * @Description: TODO
 * @author: 唐靖
 * @date: 2017年3月10日 下午2:17:02
 */
public class MessageKeyVal {
	private Map<String, Object> messageKeyVal;

	public void setMessageKeyVal(Map<String, Object> messageKeyVal) {
		this.messageKeyVal = messageKeyVal;
	}
	public Map<String, Object> getMessageKeyVal() {
		return messageKeyVal;
	}
}
