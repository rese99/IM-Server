package com.yun.im.controller;

import com.google.gson.Gson;
import com.yun.im.WebSocket.SendMessage;
import com.yun.im.config.ThreadConfig;
import com.yun.im.entity.Code;
import com.yun.im.utilies.*;
import com.yun.im.entity.User;
import com.yun.im.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.yun.im.utilies.Constants.USERID;

@Component
@RestController
public class UserInfo {
    Map<String, Code> codeMap = new HashMap<>();
    Map<String, User> userMap = new HashMap<>();
    private Gson gson = new Gson();
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThreadConfig config;

    @Autowired
    UUid uid;

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     */
    @RequestMapping(value = "/Login", method = RequestMethod.GET)
    public String login(@RequestParam("email") String email, @RequestParam("password") String password) {
        if (userRepository.updateToken(token(email), email, password) == 1) {
            User user = userRepository.login(email, password);
            if (SendMessage.connectId.contains(user.id)) {
                SendMessage.sendMessageToId(user.id, "BACK");
                SendMessage.close(user.id);
            }
            return gson.toJson(user);
        } else {
            return null;
        }
    }

    /**
     * 注册账号
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam("user") String user) {
        User user1 = gson.fromJson(user, User.class);
        user1.id = uid.uuid(user1.email);
        user1.token = token(user1.id);
        if (!USERID.contains(user1.id)) {
            userMap.put(user1.email, user1);
            config.taskExecutor().execute(() -> CodeInfo(user1.email, Constants.SignUp));
            return null;
        } else {
            return "该邮箱已被注册";
        }
    }

    /**
     * 验证码验证
     *
     * @param code
     * @param email
     * @return
     */
    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String code(@RequestParam("code") String code, @RequestParam("email") String email, @RequestParam("head") String head) {
        if (!code.equals(codeMap.get(email).code)) {
            return "验证码错误";
        }
        if (!timeout(email)) {
            return "验证码已过期";
        }
        if (head.equals(Constants.SignUp) && userRepository.register(userMap.get(email)) != 0) {
            USERID.add(userMap.get(email).id);
            return "{\"id\":\"" + userMap.get(email).id + "\"," + "\"token\":\"" + userMap.get(email).token + "\"}";
        }
        if (head.equals(Constants.forget)) {
            return email;
        }
        return "null";
    }

    /**
     * 重发验证码
     *
     * @param email
     */
    @RequestMapping(value = "/retryCode", method = RequestMethod.POST)
    public void retryCode(@RequestParam("email") String email, @RequestParam("head") String head) {
        config.taskExecutor().execute(() -> CodeInfo(email, head));
    }

    /**
     * 注销账号
     *
     * @param password
     * @param userId
     * @return
     */
    @RequestMapping(value = "/CancelAccount", method = RequestMethod.GET)
    public boolean CancelAccount(@RequestParam("password") String password, @RequestParam("userId") String userId) {
        USERID.remove(userId);
        return userRepository.DeleteUserForId(userId, password) > 0;
    }

    /**
     * 修改密码
     *
     * @param password
     * @param userId
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "/ChangePassword", method = RequestMethod.GET)
    public boolean ChangePassword(@RequestParam("password") String password, @RequestParam("userId") String userId, @RequestParam("newPassword") String newPassword) {
        return userRepository.updatePassword(newPassword, userId, password) > 0;
    }

    /**
     * 根据邮箱找回密码
     *
     * @param password
     * @param email
     * @return
     */
    @RequestMapping(value = "/ChangePasswordToId", method = RequestMethod.GET)
    public boolean ChangePasswordToId(@RequestParam("password") String password, @RequestParam("email") String email) {
        return userRepository.updatePasswordToId(email, password) > 0;
    }

    /**
     * 根据邮箱查询用户是否存在，并发送验证码
     *
     * @param email
     * @return
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.GET)
    public boolean forgetPassword(@RequestParam("email") String email) {
        if (userRepository.selectEmail(email) != null) {
            config.taskExecutor().execute(() -> CodeInfo(email, Constants.forget));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 生成验证码,并发送
     */
    private void CodeInfo(String email, String head) {
        Random random = new Random();
        Code code = new Code();
        code.code = String.valueOf(random.nextInt(900000) + 100000);
        code.email = email;
        code.time = new Date().getTime();
        codeMap.put(email, code);
        emailService.sendEmail(email, "你的验证码是[" + code.code + "],五分钟内有效，请勿告诉他人", head);
    }

    /**
     * 计算绑定的验证码是否超时
     *
     * @param email
     * @return
     */
    private boolean timeout(String email) {
        Long time = codeMap.get(email).time;
        return new Date().getTime() - time <= 5 * 60 * 1000;
    }

    /**
     * 删除过期验证码及用户信息
     */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanCode() {
        config.taskExecutor().execute(() -> {
            Iterator<Code> codeIterator = codeMap.values().iterator();
            while (codeIterator.hasNext()) {
                Code code = codeIterator.next();
                if (new Date().getTime() - code.time > 5 * 60 * 1000) {
                    userMap.remove(code.email);
                    codeIterator.remove();
                }
            }
        });
    }

    private String token(String email) {
        return String.valueOf(UUID.nameUUIDFromBytes((email + System.currentTimeMillis()).getBytes()));
    }
}
