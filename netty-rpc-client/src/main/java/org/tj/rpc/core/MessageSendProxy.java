package org.tj.rpc.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import org.tj.rpc.model.MessageRequest;

public class MessageSendProxy<T> implements InvocationHandler {

	private Class<T> cls;

	public MessageSendProxy(Class<T> cls) {
		this.cls = cls;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("来执行" + method.getName() + "的方法");
		MessageRequest request = new MessageRequest();
		request.setMessageId(UUID.randomUUID().toString());
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setTypeParameters(method.getParameterTypes());
		request.setParameters(args);

		ClientHandler handler = ClientRpcServerLoader.getInstance().getClientHandler();
		MessageCallBack callBack = handler.sendRequest(request);
		System.out.println(callBack.start() + "这个已经是结果了");
		return callBack.start();
	}
}
