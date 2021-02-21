package com.lagou.edu.factory;

import com.lagou.edu.annotation.AutoWare;
import com.lagou.edu.annotation.Component;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transcation;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.utils.ClassUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 应癫
 *
 * 工厂类，生产对象（使用反射技术）
 */

public class BeanFactory {

    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */

    private static Map<String,Object> map = new HashMap<>();  // 存储对象

    static {

        try {
            List<String> classPaths = ClassUtils.getClass("com.lagou.edu");

            for(String path : classPaths) {
                if (path.endsWith("Servlet") || path.contains("$") ) {
                    continue;
                } else {
                    Class<?> clazz = Class.forName(path);
                    String key = clazz.getSimpleName();
                    String id = key.substring(0,1).toLowerCase().concat(key.substring(1));
                    if(!clazz.isInterface()) {
                        if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)) {
                            Object o = clazz.newInstance();  // 实例化之后的对象
                            map.put(id,o);
                        }
                    }
                }
            }

            for(String path : classPaths) {
                if (path.endsWith("Servlet") || path.contains("$") ) {
                    continue;
                } else {
                    Class<?> clazz = Class.forName(path);
                    Field[] field = clazz.getDeclaredFields();

                    if(clazz.getSimpleName().equals("TransferServiceImpl")){
                        System.out.println("给TransferServiceImpl注入accountDao");
                    }

                    for(Field fiel : field) {
                        if(fiel.isAnnotationPresent(AutoWare.class)) {
                            AutoWare annotation = fiel.getAnnotation(AutoWare.class);
                            String key = fiel.getName();
                            if(!annotation.value().equals("")) {
                                key =  annotation.value().substring(0,1).toLowerCase().concat(annotation.value().substring(1));;
                            }
                            String id = clazz.getSimpleName().substring(0,1).toLowerCase().concat(clazz.getSimpleName().substring(1));
                            Object parentObject = map.get(id);
                            Method[] methods = parentObject.getClass().getMethods();
                            for (int j = 0; j < methods.length; j++) {
                                Method method = methods[j];
                                if(method.getName().equalsIgnoreCase("set" + fiel.getName())) {  // 该方法就是 setAccountDao(AccountDao accountDao)
                                    method.invoke(parentObject,map.get(key));
                                }
                            }
                            map.put(id,parentObject);
                        }
                    }
                }
            }

            for(String path : classPaths) {
                if (path.endsWith("Servlet") || path.contains("$") ) {
                    continue;
                } else {
                    Class<?> clazz = Class.forName(path);
                    if(clazz.isAnnotationPresent(Transcation.class)) {
                        ProxyFactory proxyFactory = (ProxyFactory) map.get("proxyFactory");
                        String key = clazz.getSimpleName();
                        String id = key.substring(0,1).toLowerCase().concat(key.substring(1));
                        TransferService proxy = null;
                        if(clazz.getInterfaces().length > 0) {
                            proxy = (TransferService) proxyFactory.getJdkProxy(map.get(id));
                        } else {
                            proxy = (TransferService) proxyFactory.getCglibProxy(map.get(id));
                        }
                        map.put(id, proxy);
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }



//    static {
//        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
//        // 加载xml
//        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
//        // 解析xml
//        SAXReader saxReader = new SAXReader();
//        try {
//            Document document = saxReader.read(resourceAsStream);
//            Element rootElement = document.getRootElement();
//            List<Element> beanList = rootElement.selectNodes("//bean");
//            for (int i = 0; i < beanList.size(); i++) {
//                Element element =  beanList.get(i);
//                // 处理每个bean元素，获取到该元素的id 和 class 属性
//                String id = element.attributeValue("id");        // accountDao
//                String clazz = element.attributeValue("class");  // com.lagou.edu.dao.impl.JdbcAccountDaoImpl
//                // 通过反射技术实例化对象
//                Class<?> aClass = Class.forName(clazz);
//                Object o = aClass.newInstance();  // 实例化之后的对象
//
//                // 存储到map中待用
//                map.put(id,o);
//
//            }
//
//            // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
//            // 有property子元素的bean就有传值需求
//            List<Element> propertyList = rootElement.selectNodes("//property");
//            // 解析property，获取父元素
//            for (int i = 0; i < propertyList.size(); i++) {
//                Element element =  propertyList.get(i);   //<property name="AccountDao" ref="accountDao"></property>
//                String name = element.attributeValue("name");
//                String ref = element.attributeValue("ref");
//
//                // 找到当前需要被处理依赖关系的bean
//                Element parent = element.getParent();
//
//                // 调用父元素对象的反射功能
//                String parentId = parent.attributeValue("id");
//                Object parentObject = map.get(parentId);
//                // 遍历父对象中的所有方法，找到"set" + name
//                Method[] methods = parentObject.getClass().getMethods();
//                for (int j = 0; j < methods.length; j++) {
//                    Method method = methods[j];
//                    if(method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
//                        method.invoke(parentObject,map.get(ref));
//                    }
//                }
//
//                // 把处理之后的parentObject重新放到map中
//                map.put(parentId,parentObject);
//
//            }
//
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//    }

    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static  Object getBean(String id) {
        return map.get(id);
    }

    public static Map<String, Object> getMap() {
        return map;
    }

    public static void main(String[] args) {
        BeanFactory beanFactory = new BeanFactory();
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println("key:" + entry.getKey() + " " +  "value:" + entry.getValue());
        }
    }

}
