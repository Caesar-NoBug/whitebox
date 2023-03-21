package com.caesar.model.entity;

import java.sql.Timestamp;
import java.util.Date;

public class ExtraUser extends User{
    private String name;
    private int balance;
    private int level;
    private Date birth;
    private String location;
    private String sign;
    private int follow_count;
    private int fans_count;
    private int likes;

    public ExtraUser(){

    }

    public ExtraUser(long id, String username, String password, String phone, String email, String avatar_url, Timestamp create_time, Timestamp update_time, int state, String name, int balance, int level, Date birth, String location, String sign, int follow_count, int fans_count, int likes) {
        super(id, username, password, phone, email, avatar_url, create_time, update_time, state);
        this.name = name;
        this.balance = balance;
        this.level = level;
        this.birth = birth;
        this.location = location;
        this.sign = sign;
        this.follow_count = follow_count;
        this.fans_count = fans_count;
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
