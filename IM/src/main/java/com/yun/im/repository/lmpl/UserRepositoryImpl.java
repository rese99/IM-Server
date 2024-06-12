package com.yun.im.repository.lmpl;

import com.google.gson.Gson;
import com.yun.im.entity.Emoji;
import com.yun.im.entity.Friends;
import com.yun.im.entity.Messages;
import com.yun.im.entity.User;
import com.yun.im.mapper.UserMapper;
import com.yun.im.repository.UserRepository;
import com.yun.im.utilies.Constants;
import com.yun.im.utilies.MD5;
import com.yun.im.utilies.UUid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    UserMapper userMapper;
    @Autowired
    MD5 md5;
    @Autowired
    UUid uid;

    @Override
    public int register(User user) {
        Constants.USERID.add(user.id);
        return userMapper.register(user.id, user.name, user.password, user.image, user.email, md5.md5(user.email), user.token, user.AES);
    }

    @Override
    public User login(String email, String password) {
        return userMapper.login(email, password);
    }

    @Override
    public List<Friends> GetUsers(String userId) {
        return userMapper.GetUsers(userId);
    }

    @Override
    public int messages(Messages messages) {
        return userMapper.messages(messages);
    }

    @Override
    public List<Messages> GetMessagesForId(String receiverId) {
        return userMapper.GetMessagesForId(receiverId);
    }

    @Override
    public List<Messages> GetMessages() {
        return userMapper.GetMessages();
    }

    @Override
    public int removeMessageForId(String id) {
        return userMapper.removeMessageForId(id);
    }

    @Override
    public HashSet<String> GetUserId() {
        return userMapper.GetUserId();
    }

    @Override
    public int DeleteUserForId(String userId, String password) {
        return userMapper.DeleteUserForId(userId, password);
    }

    @Override
    public int updatePassword(String newPassword, String userId, String password) {
        return userMapper.updatePassword(newPassword, userId, password);
    }

    @Override
    public List<User> getUsers(String key) {
        return userMapper.getUsers(key);
    }

    @Override
    public User getUserForId(String id) {
        return userMapper.getUserForId(id);
    }

    @Override
    public int AddFriends(String userid, String id) {
        return userMapper.AddFriends(userid, id);
    }

    @Override
    public int updateToken(String token, String email,String password) {
        return userMapper.updateToken(token,email,password);
    }

    @Override
    public String connect(String id, String token) {
        return userMapper.connect(id,token);
    }

    @Override
    public String selectEmail(String email) {
        return userMapper.selectEmail(email);
    }

    @Override
    public int updatePasswordToId(String email, String password) {
        return userMapper.updatePasswordToId(email, password);
    }

    @Override
    public int insertEmoji(Emoji emoji) {
        return userMapper.insertEmoji(emoji);
    }
}
