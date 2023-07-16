package com.api.mq.receiver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class MyMessageReceiver implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            // Process the incoming message here
            String messageContent = message.getBody(String.class);
            System.out.println("Received message: " + messageContent);
        } catch (JMSException e) {
            // Handle any exceptions that might occur during message processing
            e.printStackTrace();
        }
    }
}

