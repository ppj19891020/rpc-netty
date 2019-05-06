package com.fly.rpc.client;

import com.fly.rpc.server.service.HelloService;
import com.fly.rpc.server.service.TestService;
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

        //cglib实现动态代理
        HelloService helloService = (HelloService)rpcProxy.create(HelloService.class);
        String result = helloService.sayHello("test");
        System.out.println("============>result1: " + result);

        TestService testService = (TestService)rpcProxy.create(TestService.class);
        String result2 = testService.test("test2");
        System.out.println("============>result2: " + result2);
    }

}
