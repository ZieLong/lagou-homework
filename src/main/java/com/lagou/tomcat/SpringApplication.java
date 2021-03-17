package com.lagou.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;

/**
 * @author wz
 * @date 2021/3/15
 */
public class SpringApplication {

    public static void run() {
        Tomcat tomcat = new Tomcat();

        tomcat.setPort(8080);
        try {
            tomcat.addWebapp("/", "D:\\");
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}

