package com.lagou.edu.utils;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassUtils {

    public static List<String> getClass(String packageName) throws IOException {
        List<String> classList = new ArrayList<>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null) {
                String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    String packagePath = url.getPath();
                    addClass(classList, packagePath, packageName);
                }
            }
        }
        return classList;

    }
    public static void addClass(List<String> classList, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith("class") || file.isDirectory());
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if(StringUtils.isNotEmpty(fileName)) {
                    className = packageName + "." + className;
                }
                classList.add(className);
            } else {
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(subPackagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classList, subPackagePath, subPackageName);
            }

        }
    }


    public static void main(String[] args) throws IOException {
        List<String> clazzs = ClassUtils.getClass("com.lagou.edu");
        clazzs.forEach(r ->{
            System.out.println(r);
        });
    }

}
