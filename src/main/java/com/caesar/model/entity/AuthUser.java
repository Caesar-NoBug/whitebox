package com.caesar.model.entity;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

public class AuthUser extends User{
    private int oauthType;
    private String oauthId;
    private String oauthUnionId;
    private String oauthToken;
    private String refreshToken;
    private long expires;

    public AuthUser(){

    }

    public AuthUser(long id, String username, String password, String phone, String email, String avatar_url, Timestamp create_time, Timestamp update_time, int state, int oauthType, String oauthId, String oauthUnionId, String oauthToken, String refreshToken, long expires) {
        super(id, username, password, phone, email, avatar_url, create_time, update_time, state);
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.oauthUnionId = oauthUnionId;
        this.oauthToken = oauthToken;
        this.refreshToken = refreshToken;
        this.expires = expires;
    }

    public int getOauthType() {
        return oauthType;
    }

    public void setOauthType(int oauthType) {
        this.oauthType = oauthType;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getOauthUnionId() {
        return oauthUnionId;
    }

    public void setOauthUnionId(String oauthUnionId) {
        this.oauthUnionId = oauthUnionId;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }
}
