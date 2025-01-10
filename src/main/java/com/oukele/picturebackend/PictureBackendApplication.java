package com.oukele.picturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author oukele
 */
@SpringBootApplication
@MapperScan("com.oukele.picturebackend.mapper")
// 将exposeProxy设置为true，可在业务代码中获取代理的对象
@EnableAspectJAutoProxy(exposeProxy = true)
public class PictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureBackendApplication.class, args);
    }

}
