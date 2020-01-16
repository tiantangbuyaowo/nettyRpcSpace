package org.tj.rpc;

import java.lang.reflect.Method;
import java.util.UUID;

import org.tj.rpc.core.ClientHandler;
import org.tj.rpc.core.ClientMessageSendExecutor;
import org.tj.rpc.core.ClientRpcServerLoader;
import org.tj.rpc.core.MessageCallBack;
import org.tj.rpc.model.MessageRequest;
import org.tj.rpc.service.Calculate;

/**
 * 方便调试写的一个单线程的测试类
 * 
 * @author Administrator
 *
 */
public class ClientMain {
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InterruptedException {
		ClientMessageSendExecutor executor = new ClientMessageSendExecutor("127.0.0.1:18888");
		Calculate calc = executor.execute(Calculate.class);
		System.out.println("返回代理对象");
		int add = calc.add(8, 1);
		System.out.println(add);

		// Method method = Calculate.class.getMethod("add", int.class,
		// int.class);
		// Object[] par = new Object[] { 1, 4 };
		// System.out.println("来执行" + method.getName() + "的方法");
		// MessageRequest request = new MessageRequest();
		// request.setMessageId(UUID.randomUUID().toString());
		// request.setClassName(method.getDeclaringClass().getName());
		// request.setMethodName(method.getName());
		// request.setTypeParameters(method.getParameterTypes());
		// request.setParameters(par);
		//
		// ClientHandler handler =
		// ClientRpcServerLoader.getInstance().getClientHandler();
		// MessageCallBack callBack = handler.sendRequest(request);
		// System.out.println(callBack.start() + "这个已经是结果了");

		executor.stop();
	}
}
