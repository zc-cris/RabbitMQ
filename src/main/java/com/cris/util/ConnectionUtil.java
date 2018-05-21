package com.cris.util;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class ConnectionUtil {

    public static com.rabbitmq.client.Connection getConnection() throws IOException, TimeoutException {
        
        // 定义一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        
        // 设置rabbitMQ 服务器地址（我们使用docker 里的rabbitmq地址）
        factory.setHost("120.78.138.11");
        
        // 设置AMQP 的协议端口
        factory.setPort(5672);
        
        // 设置vhost
        factory.setVirtualHost("/vhost_cris");
        
        // 设置用户名和密码
        factory.setUsername("cris");
        factory.setPassword("123");
        
        return factory.newConnection();
    }
    
}
