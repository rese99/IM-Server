package com.yun.im.WebSocket;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.yun.im.entity.Messages;
import com.yun.im.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
@ServerEndpoint("/WebSocket/{id}")
@RestController
public class SendMessage {
    // 存储会话
    public static final ConcurrentHashMap<String, SendMessage> webSocket = new ConcurrentHashMap<String, SendMessage>();
    public static HashSet<String> messageList = new HashSet<>();
    public static final HashSet<String> connectId = new HashSet<>();
    private Gson gson = new Gson();
    private String id;
    private Session session;
    private static UserRepository userRepository;
    private static ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public void setTaskExecutor(@Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        SendMessage.taskExecutor = taskExecutor;
    }


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        SendMessage.userRepository = userRepository;
    }

    /**
     * 接入连接回调
     *
     * @param session 会话对象
     * @param id      会话ID
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        this.id = id;
        this.session = session;
        connectId.add(id);
        webSocket.put(id, this);
    }

    /**
     * 根据id查询数据库是否有其历史消息未发送
     *
     * @param id
     */
    private void retry(String id) {
        taskExecutor.execute(() -> {
            List<Messages> list = userRepository.GetMessagesForId(id);
            for (Messages messages : list) {
                sendMessageToId(id, gson.toJson(messages));
            }
            messageList.remove(id);
            userRepository.removeMessageForId(id);
        });
    }

    /**
     * 关闭连接回调
     */
    @OnClose
    public void onClose() {
        webSocket.remove(this.id);
        connectId.remove(this.id);
    }

    /**
     * 收到客户端发来消息回调
     *
     * @param message
     */
    @OnMessage(maxMessageSize = 5242880)
    public void onMessage(String message) {
        taskExecutor.execute(() -> {
            switch (message) {
                case "ping":
                    sendMessageToId(this.id, "pong");
                    break;
                case "Local":
                    if (messageList.contains(id)) {
                        retry(id);
                    }
                    break;
                default:
                    SendMessageToClient(message);
                    break;
            }
        });
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {

    }

    /**
     * 会话出现错误回调
     *
     * @param error 错误信息
     */
    @OnError
    public void onError(Throwable error) {

    }


    /**
     * 发送消息给客户端
     *
     * @param message 消息
     * @throws IOException 异常
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 给指定的会话发送消息
     *
     * @param id      会话ID
     * @param message 消息
     * @throws IOException 异常
     */
    public static void sendMessageToId(String id, String message) {
        try {
            webSocket.get(id).sendMessage(message);
        } catch (IOException e) {
            System.out.println("连接已断开");
        }
    }

    /**
     * 关闭连接，并删除连接对象
     *
     * @param id
     */
    public static void close(String id) {
        webSocket.remove(id);
        connectId.remove(id);
    }

    /**
     * 群发消息
     *
     * @param message 消息
     */
    public void sendMessageToAll(String message) {
        for (String key : webSocket.keySet()) {
            try {
                webSocket.get(key).sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ,
     * 如果存在该链接就发送，否则存入数据库
     *
     * @param messages
     */
    private void SendMessageToClient(String messages) {
        Messages message = gson.fromJson(messages, Messages.class);
        if (webSocket.containsKey(message.receiverId)) {
            sendMessageToId(message.receiverId, messages);
        } else {
            messageList.add(message.receiverId);
            userRepository.messages(message);
        }
    }
}
