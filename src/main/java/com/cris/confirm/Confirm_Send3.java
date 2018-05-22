package com.cris.confirm;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

// confirm异步模式
public class Confirm_Send3 {

    // 和事务型队列互斥
    private static final String QUEUE_NAME = "test_queue_confirm_async";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 获取一个连接
        com.rabbitmq.client.Connection connection = ConnectionUtil.getConnection();

        // 从连接中获取一个通道
        Channel channel = connection.createChannel();

        // 创建队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 将生产者设置为confirm 模式
        channel.confirmSelect();

        // 生产者需要维护一个发送的消息的序列号集合
        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

        channel.addConfirmListener(new ConfirmListener() {

            // 消息发送失败怎么处理
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Nack, SeqNo: " + deliveryTag + ", multiple:" + multiple);
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }

            // 消息发送成功怎么处理
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    System.out.println("~~~~~~~~"+ deliveryTag);
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    System.out.println("~~~~~~~~"+ deliveryTag);
                    confirmSet.remove(deliveryTag);
                }
            }
        });

        String msg = "hello,cris,i am confirm";
        for (int i = 0; i < 10; i++) {
            long no = channel.getNextPublishSeqNo();
            System.out.println("--------------------no"+no);
            
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            
            confirmSet.add(no);
        }

    }
}
