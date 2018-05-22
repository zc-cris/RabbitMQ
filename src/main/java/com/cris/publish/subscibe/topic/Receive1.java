package com.cris.publish.subscibe.topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP.BasicProperties;

// 消费者
public class Receive1 {

    private static final String QUEUE_NAME = "test_queue_topic";
    private static final String EXCHANGE_NAME = "test_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException,
            ConsumerCancelledException, InterruptedException {

        // 获取一个连接
        com.rabbitmq.client.Connection connection = ConnectionUtil.getConnection();

        // 从连接中获取一个通道
        Channel channel = connection.createChannel();

        // 创建队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 绑定消息队列到交换器
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goods.update");

        channel.basicQos(1); // 保证一次只接受一个消息

        // 创建一个消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 消息到达触发该方法
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                    throws IOException {
                System.out.println("Receive1 ----" + new String(body));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                } finally {
                    System.out.println("Receive1 has done");
                    // 手动回应消息队列已经处理好了
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        // 监听队列
        boolean autoAck = false; // 关闭自动应答
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);

    }
}
