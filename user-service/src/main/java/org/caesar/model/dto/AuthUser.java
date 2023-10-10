package org.caesar.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.caesar.model.entity.BaseUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
//包含用户基本信息和认证、授权相关信息
public class AuthUser implements UserDetails {

    private BaseUser baseUser;
    private String refreshToken;
    private List<String> permissions = new ArrayList<>();

    @JSONField(serialize = false)
    private List<GrantedAuthority> authorities;
    //TODO: 第三方信息加在此处

    @Getter
    @JSONField(serialize = false)
    private List<Integer> roles;

    public AuthUser(BaseUser baseUser, List<String> permissions, List<Integer> roles) {
        this.baseUser = baseUser;
        this.permissions = permissions;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(authorities != null)
            return authorities;

        authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return baseUser.getPassword();
    }

    @Override
    public String getUsername() {
        return baseUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}