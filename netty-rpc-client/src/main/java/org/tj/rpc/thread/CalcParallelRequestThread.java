package org.tj.rpc.thread;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.tj.rpc.core.ClientHandler;
import org.tj.rpc.core.ClientMessageSendExecutor;
import org.tj.rpc.core.ClientRpcServerLoader;
import org.tj.rpc.core.MessageCallBack;
import org.tj.rpc.model.MessageRequest;
import org.tj.rpc.service.Calculate;

public class CalcParallelRequestThread implements Runnable {

	private CountDownLatch signal;
	private CountDownLatch finish;
	private ClientMessageSendExecutor executor;
	private int taskNumber = 0;

	public CalcParallelRequestThread(ClientMessageSendExecutor executor, CountDownLatch signal, CountDownLatch finish, int taskNumber) {
		this.signal = signal;
		this.finish = finish;
		this.taskNumber = taskNumber;
		this.executor = executor;
	}

	public void run() {
		try {
			signal.await();

			//这是最原始的代码，是利用java的动态代理，来做的rpc，客户端看起来很友好，仿佛就是个在调用本地的方法
			 Calculate calc = executor.execute(Calculate.class);
			 int add = calc.add(taskNumber, taskNumber);
			 System.out.println("calc add result:[" + add + "]");

			 //这是直接操作method的方法调用远程方法， 但是这种方式需要手动指定方法名，看起来就不是很友好，但是效率更高，避免了本地的一次反射
//			Method method;
//			try {
//				method = Calculate.class.getMethod("add", int.class, int.class);
//
//				Object[] par = new Object[] { 1, 4 };
//				System.out.println("来执行" + method.getName() + "的方法");
//				MessageRequest request = new MessageRequest();
//				request.setMessageId(UUID.randomUUID().toString());
//				request.setClassName(method.getDeclaringClass().getName());
//				request.setMethodName(method.getName());
//				request.setTypeParameters(method.getParameterTypes());
//				request.setParameters(par);
//
//				ClientHandler handler = ClientRpcServerLoader.getInstance().getClientHandler();
//				MessageCallBack callBack = handler.sendRequest(request);
//				System.out.println(callBack.start() + "这个已经是结果了");
//
//			} catch (NoSuchMethodException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			finish.countDown();
		} catch (InterruptedException ex) {
			// Logger.getLogger(CalcParallelRequestThread.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
	}
}