package org.tj.rpc.core;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.tj.rpc.thread.RpcThreadPool;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 客户端连接服务器加载器
 * 
 * @author Administrator
 *
 */
public class ClientRpcServerLoader {
	private volatile static ClientRpcServerLoader clientRpcServerLoader;
	private final static String DELIMITER = ":";
	// 方法返回到Java虚拟机的可用的处理器数量
	private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;

	// netty nio线程池
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
	private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
	// 等待Netty服务端链路建立通知信号
	private Lock lock = new ReentrantLock();
	private Condition signal = lock.newCondition();
	// 客户端处理handler
	private ClientHandler clientHandler = null;

	private ClientRpcServerLoader() {
	}

	// 并发双重锁定
	public static ClientRpcServerLoader getInstance() {
		if (clientRpcServerLoader == null) {
			synchronized (ClientRpcServerLoader.class) {
				if (clientRpcServerLoader == null) {
					clientRpcServerLoader = new ClientRpcServerLoader();
				}
			}
		}
		return clientRpcServerLoader;
	}

	public void load(String serverAddress) {
		String[] ipAddr = serverAddress.split(ClientRpcServerLoader.DELIMITER);
		if (ipAddr.length == 2) {
			String host = ipAddr[0];
			int port = Integer.parseInt(ipAddr[1]);
			final InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
			threadPoolExecutor.submit(new MessageSendInitializeTask(eventLoopGroup, remoteAddr, this));
		}
	}

	public void setClientHandler(ClientHandler clientHandler) {
		try {
			lock.lock();
			this.clientHandler = clientHandler;
			// 唤醒所有等待客户端RPC线程
			signal.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public ClientHandler getClientHandler() throws InterruptedException {
		try {
			lock.lock();
			// Netty服务端链路没有建立完毕之前，先挂起等待
			if (clientHandler == null) {
				signal.await();
			}
			return clientHandler;
		} finally {
			lock.unlock();
		}
	}

	public void unLoad() {
		clientHandler.close();
		threadPoolExecutor.shutdown();
		eventLoopGroup.shutdownGracefully();
	}
}
