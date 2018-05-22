package com.cris.confirm;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

// confirm普通模式
public class Confirm_Send {

    // 和事务型队列互斥
    private static final String QUEUE_NAME = "test_queue_confirm";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 获取一个连接
        com.rabbitmq.client.Connection connection = ConnectionUtil.getConnection();

        // 从连接中获取一个通道
        Channel channel = connection.createChannel();

        // 创建队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 将生产者设置为confirm 模式
        channel.confirmSelect();
        
        String msg = "hello,cris,i am confirm";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        if(!channel.waitForConfirms()) {
            System.out.println("send fail!");
        }else {
            System.out.println("send success!");
        }
    }
}
