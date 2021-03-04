package com.tyz.rmi.core;

import com.tyz.util.XmlParse;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tyz
 */
public class RemoteMethodScanner {
    private static final Map<String, RemoteMethodDefinition> IMPL_METHOD_POOL;
    static {
        IMPL_METHOD_POOL = new HashMap<>();
    }

    /**
     * 解析描述接口和其实现类映射关系的xml文件
     * @param xmlConfigPath xml文件相对路径
     */
    public static void scan(String xmlConfigPath) {
        new XmlParse() {
            @Override
            public boolean dealElement(Element element, int i) {
                String interfaceName = element.getAttribute("name");
                String className = element.getAttribute("class");

                try {
                    Class<?> interfaceClass = Class.forName(interfaceName);
                    Class<?> implClass = Class.forName(className);
                    Object object = implClass.newInstance();

                    findImplMethod(interfaceClass, implClass, object);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }.getElement(XmlParse.getDocument(xmlConfigPath), "interface");
    }

    /**
     * 将接口中的所有方法都注册到IMPL_METHOD_POOL中，键为接口中方法的编码，值为
     * RemoteMethodDefinition。因此，根据客户端传进来的接口的方法名，就可以从
     * IMPL_METHOD_POOL中得到对应的方法和对象。
     * @param interfaceClass 接口类名称
     * @param implClass 接口实现类名称
     * @param object 对象
     * @see RemoteMethodDefinition
     */
    private static void findImplMethod(Class<?> interfaceClass, Class<?> implClass, Object object) throws NoSuchMethodException {
        Method[] interfaceMethods = interfaceClass.getDeclaredMethods();

        for (Method interfaceMethod : interfaceMethods) {
            String hashKey = String.valueOf(interfaceMethod.toString().hashCode());
            String methodName = interfaceMethod.getName();
            Class<?>[] parameterTypes = interfaceMethod.getParameterTypes();

            Method method = implClass.getMethod(methodName, parameterTypes);

            RemoteMethodDefinition md = new RemoteMethodDefinition(object, method);

            IMPL_METHOD_POOL.put(hashKey, md);
        }
    }

    /**
     * @param interfaceHashKey 接口中方法的编码
     * @return 方法的封装类
     */
    static RemoteMethodDefinition getRemoteMethodDefinition(String interfaceHashKey) {
        return IMPL_METHOD_POOL.get(interfaceHashKey);
    }
}
