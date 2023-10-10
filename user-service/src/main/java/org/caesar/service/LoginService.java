package org.caesar.service;

import java.util.Map;

public interface LoginService {
    Map<String, Object> loginUsername(String username, String password);

    Map<String, Object> loginEmail(String email, String code);

    Map<String, Object> loginPhone(String phone, String code);
}
