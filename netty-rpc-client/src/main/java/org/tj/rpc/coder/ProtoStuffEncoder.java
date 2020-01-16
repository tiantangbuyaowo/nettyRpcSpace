package org.tj.rpc.coder;

import org.tj.rpc.util.SerializationUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName: ProtoStuffEncoder
 * @Description: protostuff编码器
 * @author: 唐靖
 * @date: 2017年3月9日 下午4:25:18
 */
public class ProtoStuffEncoder extends MessageToByteEncoder<Object> {
	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2) throws Exception {
		byte[] bytes = SerializationUtils.serializer(arg1);
		arg2.writeBytes(bytes);
	}
}
