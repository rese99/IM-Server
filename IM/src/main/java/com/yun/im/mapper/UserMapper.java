package com.yun.im.mapper;

import com.yun.im.entity.Emoji;
import com.yun.im.entity.Friends;
import com.yun.im.entity.Messages;
import com.yun.im.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    User login(String email, String password);

    int register(String id, String name, String password, String image, String email,String loginemail,String token,String aes);

    List<Friends> GetUsers(String userId);

    int messages(Messages messages);

    List<Messages> GetMessagesForId(String receiverId);
    List<Messages> GetMessages();
    int removeMessageForId(String id);
    HashSet<String>GetUserId();
    int DeleteUserForId(String userId,String password);
    int updatePassword(String newPassword,String userId,String password);
    List<User>getUsers(String key);
    User getUserForId(String id);
    int AddFriends(String userid,String id);
    int updateToken(String token,String email,String password);
    String connect(String id,String token);
    String selectEmail(String email);
    int updatePasswordToId(String email, String password);
    int insertEmoji(Emoji emoji);
}
