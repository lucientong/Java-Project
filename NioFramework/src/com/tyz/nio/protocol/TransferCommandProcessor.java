package com.tyz.nio.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 解析 {@link com.tyz.nio.protocol.ETransferCommand} 的命令
 * 找到该命令对应的方法并执行。该指令一定是从客户端发出，服务器接收的，
 * 因此只需要处理客户端命令对应的方法。
 *
 * @author tyz
 */
public class TransferCommandProcessor {

    public TransferCommandProcessor() {
    }

    /**
     * 将{@code netMessage} 解析，在{@code object}对应
     * 的类中找到匹配的处理策略然后执行匹配到的方法。
     *
     * @param object 包含有命令处理策略的类的对象
     * @param netMessage 需要解析的信息
     */
    public static void resolveTransferCommandAndInvoke(Object object, NetMessage netMessage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String command = netMessage.getCommand().name();
        // process command like FORCE_DOWN
        String[] words = command.split("_");
        StringBuilder sb = new StringBuilder();

        // our method format likes { dealForceDown() }
        sb.append("deal");
        for (String word : words) {
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }

        Class<?> clazz = object.getClass();

        Method method = clazz.getDeclaredMethod(sb.toString(), NetMessage.class);
        method.invoke(clazz, netMessage);
    }
}
