package com.fly.rpc.server;

import com.fly.rpc.server.codec.RpcDecoder;
import com.fly.rpc.server.codec.RpcEncoder;
import com.fly.rpc.server.handle.RpcHandler;
import com.fly.rpc.server.handle.RpcRequest;
import com.fly.rpc.server.handle.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * rpc server端
 * @author: peijiepang
 * @date 2019-04-30
 * @Description:
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    /**
     * 端口
     */
    private int port;

    /**
     * 接口名与对象之间的映射关系
     */
    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServer(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(masterGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                        //解码器
                        .addLast(new RpcDecoder(RpcRequest.class))
                        //编码器
                        .addLast(new RpcEncoder(RpcResponse.class))
                        .addLast(new RpcHandler(handlerMap));
                    }
                })
            .option(ChannelOption.SO_BACKLOG,128)
            .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            masterGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(null != serviceMap && serviceMap.size() > 0){
            for(Object bean:serviceMap.values()){
                String beanName = bean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(beanName,bean);
            }
        }
    }
}
