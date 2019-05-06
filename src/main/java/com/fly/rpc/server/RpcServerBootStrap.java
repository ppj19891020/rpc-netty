package com.fly.rpc.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author: peijiepang
 * @date 2019-04-30
 * @Description:
 */
public class RpcServerBootStrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:/provides/application-server.xml");
    }

}
