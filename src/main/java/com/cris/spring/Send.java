package com.cris.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Send {

    public static void main(String[] args) throws InterruptedException {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");
        
        RabbitTemplate template = context.getBean(RabbitTemplate.class);
        
        // 发送消息
        template.convertAndSend("cris, i like u!");
        Thread.sleep(1000);
        context.destroy();
        
    }
}
