package com.fly.rpc.client;

import com.fly.rpc.server.service.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author: peijiepang
 * @date 2019-04-30
 * @Description:
 */
public class RpcClientBootStrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:/comsumer/application-client.xml");
        RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);
        HelloService helloService = (HelloService)rpcProxy.createByObject(HelloService.class);
        String result = helloService.sayHello("test");
        System.out.println("============>result: " + result);
    }

}
