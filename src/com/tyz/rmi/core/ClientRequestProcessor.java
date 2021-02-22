package com.tyz.rmi.core;

import com.tyz.util.ArgumentMaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;

/**
 * RMI实现的是短链接模式，当客户端需要服务端执行方法时，会和服务器建立链接。
 * ClientRequestProcessor类完成的就是和客户端建立连接之后的后续操作，包
 * 括和客户端建立通信信道，解析需要服务器执行的方法以及执行方法等。
 * @author tyz
 */
public class ClientRequestProcessor implements Runnable {
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    ClientRequestProcessor(RmiServer rmiServer, Socket clientSocket) {
        this.clientSocket = clientSocket;
        rmiServer.getThreadPool().execute(this);
    }

    /**
     * 和客户端连接以后侦听通信信道，解析客户端发送的要被远程调用的方法的信息
     * 执行后将执行结果返回给客户端。
     */
    @Override
    public void run() {
        try {
            this.dis = new DataInputStream(this.clientSocket.getInputStream());
            this.dos = new DataOutputStream(this.clientSocket.getOutputStream());

            String methodHashKey = this.dis.readUTF();
            String parameterString = this.dis.readUTF();
            RemoteMethodDefinition remoteMethodDefinition =
                        RemoteMethodScanner.getRemoteMethodDefinition(methodHashKey);

            if (remoteMethodDefinition == null) {
                throw new ClassTypeException("Can't find matched method");
            }
            Object object = remoteMethodDefinition.getObject();
            Method method = remoteMethodDefinition.getMethod();

            Object[] parameterValues = parseParameterValues(method, parameterString);

            Object result = method.invoke(object, parameterValues);

            if (method.getReturnType().equals(void.class)) {
                this.dos.writeUTF(Constant.END_MESSAGE);
            } else {
                this.dos.writeUTF(ArgumentMaker.GSON.toJson(result));
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * 将ArgumentMaker生成的字符串解析成方法的所有参数的值
     * @param method 方法
     * @param parameterString ArgumentMake生成的参数值的字符串编码
     * @return method方法的所有参数的值
     * @see ArgumentMaker
     */
    private Object[] parseParameterValues(Method method, String parameterString) {
        Parameter[] parameters = method.getParameters();
        int len = parameters.length;

        if (len <= 0) {
            return new Object[] {};
        }

        Object[] values = new Object[len];
        ArgumentMaker argumentMaker = new ArgumentMaker(parameterString);

        for (int i = 0; i < len; i++) {
            values[i] = argumentMaker.getArgument("arg" + i,
                                parameters[i].getParameterizedType());
        }
        return values;
    }

    private void close() {
        if (this.clientSocket != null && !this.clientSocket.isClosed()) {
            try {
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.clientSocket = null;
            }
        }
        if (this.dis != null) {
            try {
                this.dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.dis = null;
            }
        }
        if (this.dos != null) {
            try {
                this.dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.dos = null;
            }
        }
    }
}
