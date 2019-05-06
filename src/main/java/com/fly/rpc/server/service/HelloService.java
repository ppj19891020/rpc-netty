package com.fly.rpc.server.service;

import com.fly.rpc.server.RpcService;

/**
 * @author: peijiepang
 * @date 2019-04-30
 * @Description:
 */
@RpcService(HelloService.class)
public class HelloService {

    public String sayHello(String name){
        return "hello " + name;
    }

}
