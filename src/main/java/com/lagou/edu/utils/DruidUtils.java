package com.lagou.edu.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.lagou.edu.annotation.Component;

/**
 * @author 应癫
 */


public class DruidUtils {

    public DruidUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/bank?characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong&allowPublicKeyRetrieval=true");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
