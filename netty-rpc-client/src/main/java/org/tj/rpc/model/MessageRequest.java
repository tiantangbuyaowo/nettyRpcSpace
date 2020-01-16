package org.tj.rpc.model;

import java.util.Arrays;

/**
 * 消息请求封装对象
 * @ClassName: MessageRequest
 * @Description: TODO
 * @author: 唐靖
 * @date: 2017年3月10日 下午2:13:52
 */
public class MessageRequest {
	private String messageId;
	private String className;
	private String methodName;
	private Class<?>[] typeParameters;
	private Object[] parametersVal;

	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getTypeParameters() {
		return typeParameters;
	}
	public void setTypeParameters(Class<?>[] typeParameters) {
		this.typeParameters = typeParameters;
	}
	public Object[] getParameters() {
		return parametersVal;
	}
	public void setParameters(Object[] parametersVal) {
		this.parametersVal = parametersVal;
	}
	@Override
	public String toString() {
		return "MessageRequest [messageId=" + messageId + ", className=" + className + ", methodName=" + methodName
				+ ", typeParameters=" + Arrays.toString(typeParameters) + ", parametersVal="
				+ Arrays.toString(parametersVal) + "]";
	}
}
