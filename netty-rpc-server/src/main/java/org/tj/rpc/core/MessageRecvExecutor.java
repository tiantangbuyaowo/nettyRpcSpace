package org.tj.rpc.core;

import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.tj.rpc.annotation.RpcService;
import org.tj.rpc.core.coder.ProtoStuffDecoder;
import org.tj.rpc.core.coder.ProtoStuffEncoder;
import org.tj.rpc.model.MessageRequest;
import org.tj.rpc.thread.NamedThreadFactory;
import org.tj.rpc.thread.RpcThreadPool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class MessageRecvExecutor implements ApplicationContextAware, InitializingBean {
	private String serverAddress;
	private final static String DELIMITER = ":";
	private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();
	private static ThreadPoolExecutor threadPoolExecutor;

	public MessageRecvExecutor(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public static void submit(Runnable task) {
		if (threadPoolExecutor == null) {
			synchronized (MessageRecvExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
				}
			}
		}
		threadPoolExecutor.submit(task);
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		// MessageKeyVal keyVal = (MessageKeyVal)
		// ctx.getBean(Class.forName("org.tj.rpc.core.MessageKeyVal"));
		// Map<String, Object> rpcServiceObject = keyVal.getMessageKeyVal();
		// Set s = rpcServiceObject.entrySet();
		// Iterator<Map.Entry<String, Object>> it = s.iterator();
		// Map.Entry<String, Object> entry;
		// while (it.hasNext()) {
		// entry = it.next();
		// handlerMap.put(entry.getKey(), entry.getValue());
		// }
		// 获取所有带有 RpcService 注解的 Spring Bean
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		for (Object serviceBean : serviceBeanMap.values()) {
			String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
			handlerMap.put(interfaceName, serviceBean);
		}
	}

	public void afterPropertiesSet() throws Exception {
		// netty的线程池模型设置成主从线程池模式，这样可以应对高并发请求
		// 当然netty还支持单线程、多线程网络IO模型，可以根据业务需求灵活配置
		ThreadFactory threadRpcFactory = new NamedThreadFactory("NettyRPC ThreadFactory");
		// 方法返回到Java虚拟机的可用的处理器数量
		int parallel = Runtime.getRuntime().availableProcessors() * 2;
		initNetty(parallel, threadRpcFactory);
	}

	/**
	 * 初始化netty
	 * 
	 * @Title: initNetty
	 * @Description: TODO
	 * @author: 唐靖
	 * @param selectorProvider
	 * @param threadRpcFactory
	 * @param parallel
	 * @return: void
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 */
	private void initNetty(int parallel, ThreadFactory threadRpcFactory)
			throws NumberFormatException, InterruptedException {
		String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup(parallel, threadRpcFactory, SelectorProvider.provider());
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel channel) throws Exception {
							channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
									.addLast(new ProtoStuffDecoder(MessageRequest.class))
									.addLast(new LengthFieldPrepender(2)) // 将RPC请求进行解码（为了处理请求）
									// 将RPC响应进行编码（为了返回响应）
									.addLast(new ProtoStuffEncoder())
									// 处理 RPC请求
									.addLast(new ServerHandler(handlerMap));
						}
					}); // 绑定编码解码器
			// 绑定端口，同步等待成功
			ChannelFuture f = b.bind(Integer.valueOf(ipAddr[1])).sync();
			// 等待服务器监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
