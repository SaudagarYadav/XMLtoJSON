package com.api.mq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.api.mq.receiver.MyMessageReceiver;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MyMessageReceiver myMessageReceiver;

    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // Set any other JMS configuration here if needed
        return factory;
    }

    @Bean
    public MyMessageReceiver myMessageReceiver() {
        return new MyMessageReceiver();
    }
}

