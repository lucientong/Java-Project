package com.tyz.rmi.core;

import com.google.gson.Gson;
import com.tyz.util.ArgumentMaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;

/**
 * RMI 客户端
 *
 * @author tyz
 */
public class RmiClient implements IRmiInit {
    private int rmiServerPort;
    private String rmiServerIp;

    private DataInputStream dis;
    private DataOutputStream dos;

    private Socket socket;

    public RmiClient() {
        this(RmiInitializer.DEFAULT_PORT, RmiInitializer.DEFAULT_IP);
    }

    public RmiClient(int rmiServerPort, String rmiServerIp) {
        this.rmiServerPort = rmiServerPort;
        this.rmiServerIp = rmiServerIp;
    }

    /**
     * 调用 RmiInitializer 类实现服务器端口号和IP地址的初始化
     * @param configFilePath properties配置文件路径
     */
    public void initRmiClient(String configFilePath) {
        RmiInitializer.initialize(this, configFilePath);
    }

    /**
     * 与服务器建立短链接，将需要执行的方法发给服务器端，由服务器执行之后将结果
     * 传回，客户端接收之后将结果返回。
     * @param method 要被代理执行的方法
     * @param args 方法对应的参数值
     * @return 方法执行的结果
     *
     * 服务器要执行一个方法，需要三个参数：对象，方法，参数。
     * 对象的获得：客户端存留的是一个接口，但是服务器要执行方法，必须有一个接口的实现类。
     *           因此，我们需要根据客户端传递的接口信息找到实现类。这里采用xml文件映射
     *           的方式来完成。映射为 接口名-实现类名，通过RemoteMethodScanner类扫
     *           描配置的xml文件得到一个散列表。
     *           接口的信息由RmiProxy通过参数进行传递，这里有两个思路，一个是直接将接
     *           口和方法一起传递，然后发送给服务器；一个是只发送方法的信息，通过方法来
     *           在服务器端解析出接口类，从而得到实现类。
     *           考虑到通信信道的传递效率的问题，尽量减少传输的信息，在这里采用只传递方
     *           法的信息，服务器对信息进行解码得到接口的方式。
     * 方法的获得：客户端的代理类，RmiProxy直接将方法传递给RmiClient，由客户端将方法的
     *           编码通过通信信道发送给 ClientRequestProcessor。
     * 参数的获得：通过ArgumentMaker类来获取。
     * @see RmiProxy
     * @see ArgumentMaker
     * @see RemoteMethodScanner
     * @see ClientRequestProcessor
     */
    @SuppressWarnings("unchecked")
    <T> T methodInvoker(Method method, Object[] args) throws IOException {
        this.socket = new Socket(this.rmiServerIp, this.rmiServerPort);
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.dos = new DataOutputStream(this.socket.getOutputStream());

        this.dos.writeUTF(String.valueOf(method.toString().hashCode()));
        this.dos.writeUTF(getParameterString(args));

        String result = this.dis.readUTF();
        if (result.equals(Constant.END_MESSAGE)) {
            close();
            return null;
        }

        Type returnType = method.getGenericReturnType();
        close();

        return (T) ArgumentMaker.GSON.fromJson(result, returnType);
    }

    /**
     * 使用ArgumentMake类将方法执行所需要的参数值编码
     * @param args 参数集
     * @return 参数集的编码
     * @see ArgumentMaker
     */
    private String getParameterString(Object[] args) {
        if (args == null) {
            return Constant.NO_ARGS;
        }
        Gson gson = ArgumentMaker.GSON;
        ArgumentMaker argumentMaker = new ArgumentMaker();

        for (int i = 0; i < args.length; i++) {
            argumentMaker.addArg("arg" + i, args[i]);
        }
        return argumentMaker.toString();
    }

    /**
     * 关闭客户端的socket和通信信道
     */
    private void close() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.socket = null;
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

    @Override
    public void setPort(int port) {
        this.rmiServerPort = port;
    }

    @Override
    public void setIp(String ip) {
        this.rmiServerIp = ip;
    }
}
