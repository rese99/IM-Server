package com.yun.im.utilies;

import com.yun.im.WebSocket.SendMessage;
import com.yun.im.entity.Messages;
import com.yun.im.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Override
    public void run(String... args) {
        Constants.USERID = userRepository.GetUserId();
        List<Messages> list = userRepository.GetMessages();
        if (null != list) {
            for (Messages messages : list) {
                SendMessage.messageList.add(messages.receiverId);
            }
        }
    }
}
