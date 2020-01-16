package org.tj.rpc.core;

import java.net.InetSocketAddress;

import org.tj.rpc.coder.ProtoStuffDecoder;
import org.tj.rpc.coder.ProtoStuffEncoder;
import org.tj.rpc.model.MessageResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 客户端发送消息的线程
 * 
 * @author Administrator
 *
 */
public class MessageSendInitializeTask implements Runnable {

	private EventLoopGroup eventLoopGroup = null;
	private InetSocketAddress serverAddress = null;
	private ClientRpcServerLoader loader = null;

	MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress,
			ClientRpcServerLoader loader) {
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.loader = loader;
	}

	public void run() {
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
						.addLast(new ProtoStuffDecoder(MessageResponse.class)).addLast(new LengthFieldPrepender(2)) // 将RPC请求进行解码（为了处理请求）
						// 将RPC响应进行编码（为了返回响应）
						.addLast(new ProtoStuffEncoder())
						// 处理 RPC请求
						.addLast(new ClientHandler());

			}
		});

		ChannelFuture channelFuture = b.connect(serverAddress);
		channelFuture.addListener(new ChannelFutureListener() {
			public void operationComplete(final ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					ClientHandler handler = channelFuture.channel().pipeline().get(ClientHandler.class);
					MessageSendInitializeTask.this.loader.setClientHandler(handler);
				}
			}
		});
	}
}
