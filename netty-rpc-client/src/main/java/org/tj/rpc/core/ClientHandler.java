package org.tj.rpc.core;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.tj.rpc.model.MessageRequest;
import org.tj.rpc.model.MessageResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter {
	private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();
	private volatile Channel channel;
	private SocketAddress remoteAddr;

	public Channel getChannel() {
		return channel;
	}

	public SocketAddress getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * 连接活动时
	 * 
	 * @author 唐靖
	 *
	 * @date 2017年2月6日下午10:02:37
	 *
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.remoteAddr = this.channel.remoteAddress();
	}

	/**
	 * 连接服务器成功
	 */
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.channel = ctx.channel();
	}

	/**
	 * 消息读取时
	 * 
	 * @author 唐靖
	 *
	 * @date 2017年2月6日下午10:03:05
	 *
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageResponse response = (MessageResponse) msg;
		String messageId = response.getMessageId();
		MessageCallBack callBack = mapCallBack.get(messageId);
		if (callBack != null) {
			mapCallBack.remove(messageId);
			callBack.over(response);
		}
	}

	/**
	 * 异常时
	 * 
	 * @author 唐靖
	 *
	 * @date 2017年2月6日下午10:03:16
	 *
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}

	public MessageCallBack sendRequest(MessageRequest request) {
		MessageCallBack callBack = new MessageCallBack(request);
		mapCallBack.put(request.getMessageId(), callBack);
		channel.writeAndFlush(request);
		return callBack;
	}

	/**
	 * 关闭通道
	 */
	public void close() {
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
}
