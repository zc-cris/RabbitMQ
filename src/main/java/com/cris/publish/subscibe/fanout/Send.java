package com.cris.publish.subscibe.fanout;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.Channel;

//消息生产者
public class Send {

    private static final String EXCHANGE_NAME = "test_queue_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取一个连接
        com.rabbitmq.client.Connection connection = ConnectionUtil.getConnection();

        // 从连接中获取一个通道
        Channel channel = connection.createChannel();

        // 交换器声明
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        /*
         * 每个消费者发送确认消息到消息队列之前，消息队列不会发送下一个消息到消费者，即消费者和消息队列形成了一应一答
         * 限制消息队列发送给同一个消费者的消息不会超过一条（一次来回交流中），消费者一次只处理一次消息
         */
        int prefetchCount = 1;
        channel.basicQos(1);

        String msg = "hello,cris,i am exchange...";
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
        System.out.println("发送信息成功" + msg);

        channel.close();
        connection.close();

    }

}
