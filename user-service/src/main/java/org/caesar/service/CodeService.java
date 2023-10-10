package org.caesar.service;

import org.caesar.model.vo.Response;

public interface CodeService {
    Response sendLoginEmailCode(String email);

    Response sendLoginPhoneCode(String email);

    Response sendResetEmailCode(String email);

    Response sendResetPhoneCode(String email);

    Response sendRegisterEmailCode(String email);
}
