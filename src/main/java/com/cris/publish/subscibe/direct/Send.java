package com.cris.publish.subscibe.direct;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

    private static final String EXCHANGE_NAME = "test_exchange_direct";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明创建一个交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        
        /*
         * 每个消费者发送确认消息到消息队列之前，消息队列不会发送下一个消息到消费者，即消费者和消息队列形成了一应一答
         * 限制消息队列发送给同一个消费者的消息不会超过一条（一次来回交流中），消费者一次只处理一次消息
         */
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        String mString = "hello,cirs, i am direct mode";
        String routingKey = "info"; // 设置消息的路由键
        // 将消息发送到交换器
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, mString.getBytes());
        System.out.println("消息发送成功！"+mString);

        channel.close();
        connection.close();

    }

}
