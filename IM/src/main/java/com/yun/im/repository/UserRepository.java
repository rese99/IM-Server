package com.yun.im.repository;

import com.yun.im.entity.Emoji;
import com.yun.im.entity.Friends;
import com.yun.im.entity.Messages;
import com.yun.im.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;


public interface UserRepository {
    public int register(User user);
    public User login(String email,String password);
    public List<Friends> GetUsers(String userId);
    public int messages(Messages messages);
    public List<Messages> GetMessagesForId(String receiverId);
    public List<Messages> GetMessages();
    public int removeMessageForId(String id);
    public HashSet<String> GetUserId();
    public int DeleteUserForId(String userId,String email);
    public int updatePassword(String newPassword,String userId,String password);
    public List<User>getUsers(String key);
    public User getUserForId(String id);
    public int AddFriends(String userid,String id);
    public int updateToken(String token,String email,String password);
    public String connect(String id,String token);
    public String selectEmail(String email);
    public int updatePasswordToId(String email, String password);
    public int insertEmoji(Emoji emoji);
}
