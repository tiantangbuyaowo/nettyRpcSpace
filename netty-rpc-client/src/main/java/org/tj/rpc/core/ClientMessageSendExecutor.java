package org.tj.rpc.core;

import java.lang.reflect.Proxy;

public class ClientMessageSendExecutor {
	private ClientRpcServerLoader loader = ClientRpcServerLoader.getInstance();

	public ClientMessageSendExecutor(String serverAddress) {
		loader.load(serverAddress);
	}

	public void stop() {
		loader.unLoad();
	}

	@SuppressWarnings("unchecked")
	public static <T> T execute(Class<T> rpcInterface) {
		return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class<?>[] { rpcInterface },
				new MessageSendProxy<T>(rpcInterface));
	}
}
