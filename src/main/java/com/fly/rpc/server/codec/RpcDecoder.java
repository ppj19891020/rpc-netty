package com.fly.rpc.server.codec;

import com.fly.rpc.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * rpc 解码器-解决拆包和粘包问题
 * 协议: 4个字节长度+body内容
 * @author: peijiepang
 * @date 2019-04-30
 * @Description:
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //如果接收到的字节还不到4个字节,也即是连消息长度字段中的内容都不完整的,直接return
        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();

        //包长度
        int dataLength = in.readInt();

        if(dataLength < 0){
            ctx.close();
        }

        int beginIndex = in.readerIndex();

        //对于拆包这种场景,由于还未读取到完整的消息,bufferIn.readableBytes() 会小于length,
        //并重置bufferIn的readerIndex为0,然后退出,ByteToMessageDecoder会乖乖的等待下个包的到来。
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = SerializationUtil.deSerialize(data, genericClass);
        out.add(obj);
    }
}
