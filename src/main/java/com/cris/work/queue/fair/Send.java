package com.cris.work.queue.fair;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

// 消息生产者
public class Send {

    private static final String QUEUE_NAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取一个连接
        com.rabbitmq.client.Connection connection = ConnectionUtil.getConnection();

        // 从连接中获取一个通道
        Channel channel = connection.createChannel();

        // 创建队列声明（代码创建队列）
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        /*
         * 每个消费者发送确认消息到消息队列之前，消息队列不会发送下一个消息到消费者，即消费者和消息队列形成了一应一答
         * 限制消息队列发送给同一个消费者的消息不会超过一条（一次来回交流中），消费者一次只处理一次消息
         */
        int prefetchCount = 1;
        channel.basicQos(1);

        for (int i = 0; i < 50; i++) {
            
            String msg = "hello,cris" +i;
            System.out.println("send msg : " + msg);
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        }
        channel.close();
        connection.close();

    }

}
