package com.yun.im.entity;

public class Messages {
    public String message;
    public String receiverId;
    public String timestamp;
    public String senderId;
    public String message_type;

    @Override
    public String toString() {
        return "Messages{" +
                "message='" + message + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", senderId='" + senderId + '\'' +
                '}';
    }
}
