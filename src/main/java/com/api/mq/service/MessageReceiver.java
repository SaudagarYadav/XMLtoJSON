package com.api.mq.service;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@EnableJms
class MessageReceiver {

    // Define the queue name you want to listen to
    private static final String QUEUE_NAME = "QUEUE.NAME";

    // Listener method for receiving messages
    @JmsListener(destination = QUEUE_NAME)
    public void receiveMessage(String message) {
        // Process the received message here
        System.out.println("Received message: " + message);
    }
}
