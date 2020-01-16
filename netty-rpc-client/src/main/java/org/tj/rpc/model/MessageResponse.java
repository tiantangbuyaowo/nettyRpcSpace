package org.tj.rpc.model;

/**
 * 消息应答对象
 * @ClassName: MessageResponse
 * @Description: TODO
 * @author: 唐靖
 * @date: 2017年3月10日 下午2:15:34
 */
public class MessageResponse {
	private String messageId;
	private String error;
	private Object resultDesc;

	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Object getResult() {
		return resultDesc;
	}
	public void setResult(Object resultDesc) {
		this.resultDesc = resultDesc;
	}
	@Override
	public String toString() {
		return "MessageResponse [messageId=" + messageId + ", error=" + error + ", resultDesc=" + resultDesc + "]";
	}
}
