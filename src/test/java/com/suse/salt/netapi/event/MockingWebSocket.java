package com.suse.salt.netapi.event;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * This class is intended to emulate the Server WebSocket
 * EndPoint where EventStream is connected to, and where events came from.
 */
@ServerEndpoint(value = "/token")
public abstract class MockingWebSocket {

    /**
     *
     */
    public static class Message {
        private String message;
        private Optional<Long> delay;

        public Message(String message, long delay) {
            this.message = message;
            this.delay = Optional.of(delay);
        }

        public Message(String message) {
            this.message = message;
            this.delay = Optional.empty();
        }

        public Optional<Long> getDelay() {
            return delay;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * return the messages to send
     * @return messages
     */
    public abstract Stream<Message> messages();

    public MockingWebSocket() {
    }

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        messages().forEach(m -> {
            m.getDelay().ifPresent(s -> {
                try {
                    Thread.sleep(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            try {
                session.getBasicRemote().sendText(m.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        });
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }
}