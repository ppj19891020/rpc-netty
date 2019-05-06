package com.fly.rpc.client;

import com.fly.rpc.server.codec.RpcDecoder;
import com.fly.rpc.server.codec.RpcEncoder;
import com.fly.rpc.server.handle.RpcRequest;
import com.fly.rpc.server.handle.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: peijiepang
 * @date 2019-05-05
 * @Description:
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;

    private int port;

    private RpcResponse response;

    //jvm 锁
    private final Object lock = new Object();

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        this.response = msg;
        synchronized (lock){
            lock.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        LOGGER.error("client caught error",cause);
    }

    /**
     * 发送消息
     * @param request
     * @return
     */
    public RpcResponse send(RpcRequest request){
        EventLoopGroup work = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /*将RPC请求进行编码（发送请求）*/
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    /*将RPC响应进行解码（返回响应）*/
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    /*使用RpcClient发送RPC请求*/
                                    .addLast(RpcClient.this);

                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            channelFuture.channel().writeAndFlush(request).sync();

            synchronized (lock){
                lock.wait();
            }
            if(null != response){
                channelFuture.channel().closeFuture().sync();
            }
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }
        return null;
    }
}
