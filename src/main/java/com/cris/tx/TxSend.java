package com.cris.tx;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cris.util.ConnectionUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

// 消息生产者
public class TxSend {

    private static final String QUEUE_NAME = "test_queue_tx";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取一个连接
        com.rabbitmq.client.Connection connection = ConnectionUtil.getConnection();

        // 从连接中获取一个通道
        Channel channel = connection.createChannel();

        // 创建队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String msg = "hello,cris,i am tx";

        // 开启事务模式
        try {
            channel.txSelect();
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            int i = 1/0;
            channel.txCommit();     // 事务提交最好放在最后面执行
        } catch (Exception e) {
            channel.txRollback();
            e.printStackTrace();
            System.out.println("-----------");
        }finally{
            System.out.println("send msg : " + msg);
            channel.close();
            connection.close();
        }

    }

}
