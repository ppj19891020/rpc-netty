package com.fly.rpc.client;

import com.fly.rpc.server.handle.RpcRequest;
import com.fly.rpc.server.handle.RpcResponse;
import net.sf.cglib.proxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author: peijiepang
 * @date 2019-05-05
 * @Description:
 */
public class RpcProxy {

    private final static Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    /**
     * 服务器列表
     */
    private List<String> serverList;

    public Object createByObject(Class clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ObjectMethodInterceptor());
        return enhancer.create();
    }

    /**
     * 类-动态代理
     */
    class ObjectMethodInterceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            RpcRequest request = new RpcRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setClassName(method.getDeclaringClass());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            //随机取一台服务器
            int random = new Random().nextInt(serverList.size());
            String server = serverList.get(random);

            String[] array = server.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            RpcClient client = new RpcClient(host,port);

            LOGGER.info("request:{}",request.toString());
            RpcResponse response = client.send(request);

            if (response == null) {
                throw new RuntimeException("response is null...");
            }
            //返回RPC响应结果
            if(response.hasError()){
                throw response.getError();
            }else {
                return response.getResult();
            }
        }
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }
}
