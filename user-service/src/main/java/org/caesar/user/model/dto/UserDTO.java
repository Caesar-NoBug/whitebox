package org.caesar.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.caesar.user.model.po.UserPO;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//包含用户基本信息和认证、授权相关信息
public class UserDTO /*implements UserDetails*/ {

    private UserPO userPO;
    private String refreshToken;
    private List<String> permissions = new ArrayList<>();

    /*@JsonIgnore
    private List<GrantedAuthority> authorities;*/
    //TODO: 第三方信息加在此处

    @Getter
    @JsonIgnore
    private List<Integer> roles;

    public UserDTO(UserPO userPO, List<String> permissions, List<Integer> roles) {
        this.userPO = userPO;
        this.permissions = permissions;
        this.roles = roles;
    }

   /* @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(authorities != null)
            return authorities;

        authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return userPO.getPassword();
    }

    @Override
    public String getUsername() {
        return userPO.getUsername();
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
    }*/
}