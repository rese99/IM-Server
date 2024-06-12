package com.yun.im.entity;

public class User {
    public String name;
    public String password;
    public String image;
    public String email;
    public String id;
    public String token;
    public String AES;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
