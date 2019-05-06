package com.fly.rpc.server.handle;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * rpc 处理类
 * @author: peijiepang
 * @date 2019-04-30
 * @Description:
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String,Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setReponseId(request.getRequestId());
        try {
            Object serviceBean = handlerMap.get(request.getClassName().getName());
            Method method = serviceBean.getClass().getDeclaredMethod(request.getMethodName(),request.getParameterTypes());
            if(null != method){
                Object result = method.invoke(serviceBean,request.getParameters());
                response.setResult(result);
            }
        } catch (Exception ex) {
            response.setError(ex);
        }
        LOGGER.info("response:{}", response.toString());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
       LOGGER.error("server cause error",cause);
    }
}
