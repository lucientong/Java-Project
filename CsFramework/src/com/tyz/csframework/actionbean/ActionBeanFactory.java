package com.tyz.csframework.actionbean;

import com.tyz.csframework.annotation.Action;
import com.tyz.csframework.annotation.ActionMapping;
import com.tyz.csframework.annotation.ActionParameter;
import com.tyz.csframework.annotation.LostAnnotationException;
import com.tyz.util.PackageScanner;
import com.tyz.util.TypeParser;
import com.tyz.util.XmlParse;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描指定的包里的类文件，找到注解有
 * {@link com.tyz.csframework.annotation.Action}的类，
 * 在这个类中找到注解有
 * {@link com.tyz.csframework.annotation.ActionMapping}
 * 的方法，将这个方法注册到 {@link ActionBeanFactory}
 * ，以方法的 {@code ActionMapping} 注解的值action
 * 为键，和方法的封装类 {@link ActionBeanDefinition}
 * 映射。
 *
 * @author tyz
 */
public class ActionBeanFactory {
    private static final Map<String, ActionBeanDefinition> ACTION_BEAN_FACTORY;
    static {
        ACTION_BEAN_FACTORY = new HashMap<>();
    }

    public ActionBeanFactory() {}

    /**
     * 根据对象注册bean
     *
     * @param object 需要注册映射方法的对象
     * @throws LostAnnotationException 需要注册的方法缺少参数注解
     */
    public static void addActionBean(Object object) throws LostAnnotationException {
        processMethod(object, object.getClass());
    }

    /**
     * 扫描用户提供的包名，找到包中所有被 {@link Action} 注解过的类，
     * 在找到的类中找到所有被 {@link ActionMapping} 注解过的方法，
     * 将这个方法与对应的action映射加到 {@code ACTION_BEAN_FACTORY}
     *
     * @param packageName 需要扫描的包名
     */
    public static void scanActionFromAnnotation(String packageName) {
        new PackageScanner() {
            @Override
            public void dealClass(Class<?> clazz) {
                // 忽略所有的接口、注解类、枚举以及没有Action注解的类
                if (clazz.isInterface() || clazz.isAnnotation()
                    || clazz.isEnum() || !clazz.isAnnotationPresent(Action.class)) {
                    return;
                }
                try {
                    //根据类生成对象
                    Object object = clazz.newInstance();
                    processMethod(object, clazz);
                } catch (InstantiationException | IllegalAccessException | LostAnnotationException e) {
                    e.printStackTrace();
                }
            }
        }.packageScanner(packageName);
    }

    /**
     * 扫描用户提供的xml文件，找到所有将action与可执行方法的映射并将其加到
     * {@code ACTION_BEAN_FACTORY} 中
     *
     * @param xmlFilePath xml配置文件的相对路径
     */
    public static void scanActionFromXmlFile(String xmlFilePath) {
        new XmlParse() {
            @Override
            public boolean dealElement(Element element, int i) {
                String actionName = element.getAttribute("name");
                if (ACTION_BEAN_FACTORY.containsKey(actionName)) {
                    return false;
                }
                //获取方法名和类名
                String methodName = element.getAttribute("method");
                String className = element.getAttribute("class");

                try {
                    //得到方法名对应的类并生成对象
                    Class<?> clazz = Class.forName(className);
                    Object object = clazz.newInstance();

                    ActionBeanDefinition bean = new ActionBeanDefinition(object);

                    //获取参数，并将参数注入进bean
                    new XmlParse() {
                        @Override
                        public boolean dealElement(Element element, int i) {
                            return addParameters(element, bean);
                        }
                    }.getElement(element, "parameter");

                    //根据注入好的参数和方法名，将方法注入进bean中
                    injectMethodIntoBean(methodName, bean);
                    //将注入好的bean注册进factory中
                    ACTION_BEAN_FACTORY.put(actionName, bean);

                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.getElement(XmlParse.getDocument(xmlFilePath), "action");
    }

    /**
     * 将 {@code method} 中的参数添加到 {@code bean} 中
     *
     * @param bean 要注入的bean
     * @param method 被 {@link ActionMapping} 注解的方法
     * @throws LostAnnotationException 方法缺少 {@link ActionParameter} 注解
     */
    private static void processMethodParameters(ActionBeanDefinition bean, Method method) throws LostAnnotationException {
        Parameter[] parameters = method.getParameters();
        int index = 1;

        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(ActionParameter.class)) {
                throw new LostAnnotationException(index +  "st parameter is lost [ActionParameter] annotation.");
            }
            ActionParameter actionParameter = parameter.getAnnotation(ActionParameter.class);
            Type type = parameter.getParameterizedType();
            String name = actionParameter.name();

            ParameterDefinition pd = new ParameterDefinition(name, type);

            bean.addParameter(pd);

            index++;
        }
    }

    /**
     * 得到 {@code clazz} 类下所有被 {@link ActionMapping} 注解的方法，
     * 完成和action的映射。
     *
     * @param object 被 {@link Action} 注解的类所生成的对象
     * @param clazz 被 {@link Action} 注解的类
     */
    private static void processMethod(Object object, Class<?> clazz) throws LostAnnotationException {
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(ActionMapping.class)) {
                continue;
            }
            //根据方法的注解取得对应的action
            ActionMapping actionMapping = method.getAnnotation(ActionMapping.class);
            String action = actionMapping.action();

            ActionBeanDefinition bean = new ActionBeanDefinition(object, method);

            processMethodParameters(bean, method);

            ACTION_BEAN_FACTORY.put(action, bean);
        }
    }

    /**
     * 将xml文件中配置的参数添加到 {@code bean} 中
     *
     * @param element 当前遍历到的xml元素
     * @param bean 需要被注入参数的bean
     * @return 是否添加成功
     */
    private static boolean addParameters(Element element, ActionBeanDefinition bean) {
        String name = element.getAttribute("name");
        String type = element.getAttribute("type");

        ParameterDefinition pd = new ParameterDefinition(name, TypeParser.strToType(type));

        bean.addParameter(pd);

        return true;
    }

    /**
     * 根据 {@code bean} 中的参数类型，方法名 {@code methodName}，以及
     * {@code bean} 中的 {@code object} 获取对应的方法，将其注入进 {@code bean} 中
     *
     * @param methodName 需要得到的方法的方法名
     * @param bean 被注入好参数的bean
     * @throws NoSuchMethodException 未找到对应方法
     */
    private static void injectMethodIntoBean(String methodName, ActionBeanDefinition bean) throws NoSuchMethodException {
        Class<?>[] types = bean.getParameterTypes();
        Class<?> clazz = bean.getObject().getClass();

        Method method = clazz.getMethod(methodName, types);
        bean.setMethod(method);
    }

    /**
     * 根据用户提供的行为 {@code action}，在注册到 {@code ACTION_BEAN_FACTORY}
     * 的方法中寻找映射的方法。
     *
     * @param action 行为
     * @return action映射的方法
     */
    static ActionBeanDefinition getActionBeanDefinition(String action) {
        return ACTION_BEAN_FACTORY.get(action);
    }
}
