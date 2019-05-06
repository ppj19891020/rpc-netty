package com.fly.rpc.server.service.impl;

import com.fly.rpc.server.RpcService;
import com.fly.rpc.server.service.TestService;

/**
 * @author: peijiepang
 * @date 2019-05-06
 * @Description:
 */
@RpcService(value = TestService.class)
public class TestServiceImpl implements TestService {
    @Override
    public String test(String msg) {
        return "response:"+msg;
    }
}
