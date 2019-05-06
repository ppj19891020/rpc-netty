package com.fly.rpc.server.handle;

import java.io.Serializable;
import java.util.Arrays;

/**
 * RPC请求封装Model
 */
public class RpcRequest implements Serializable{
    private static final long serialVersionUID = -2625585669090924236L;

    /*请求ID*/
    private String requestId;
    /*调用class类名*/
    private Class<?> className;
    /*调用方法名*/
    private String methodName;
    /*调用参数类型集合*/
    private Class<?>[] parameterTypes;
    /*调用参数集合*/
    private Object[] parameters;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className=" + className +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
