package org.caesar.user.service;

public interface CodeService {
    void sendLoginEmailCode(String email);

    void sendLoginPhoneCode(String email);

    void sendResetEmailCode(String email);

    void sendResetPhoneCode(String email);

    void sendRegisterEmailCode(String email);
}
