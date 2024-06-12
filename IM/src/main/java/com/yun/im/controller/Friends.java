package com.yun.im.controller;

import com.google.gson.Gson;
import com.yun.im.entity.User;
import com.yun.im.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RestController
public class Friends {
    @Autowired
    UserRepository userRepository;
    Gson gson = new Gson();

    @RequestMapping(value = "/GetUsers", method = RequestMethod.GET)
    public String GetUsers(@RequestParam("userId") String userId) {
        return gson.toJson(userRepository.GetUsers(userId));
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public String getUsers(@RequestParam("userId") String userId, @RequestParam("key") String key) {
        List<User> users = userRepository.getUsers(key);
        if (null != users) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id.equals(userId)) {
                    users.remove(i);
                    break;
                }
            }
        }
        return gson.toJson(users);
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public String getUser(@RequestParam("userId") String userId) {
        return gson.toJson(userRepository.getUserForId(userId));
    }

    @RequestMapping(value = "/AddUser", method = RequestMethod.GET)
    public int AddUser(@RequestParam("userid") String userid, @RequestParam("id") String id) {
        return userRepository.AddFriends(userid, id);
    }
}
