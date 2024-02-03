package org.caesar.user.service.impl;

import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.user.constant.CacheKey;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.user.model.entity.User;
import org.caesar.user.repository.UserRepository;
import org.caesar.user.service.CodeService;
import org.caesar.common.redis.RedisCache;
import org.caesar.common.str.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CodeServiceImpl implements CodeService {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private UserRepository userRepo;

    @Resource
    private RedisCache redisCache;

    public static final int VALIDATION_CODE_EXPIRE = 5;

    @Override
    public void sendLoginEmailCode(String email){

        User user = userRepo.selectUserByEmail(email);

        //查无此人
        ThrowUtil.ifNull(user, "The email does not match any user account!");

        String redisKey = CacheKey.AUTH_CODE_EMAIL + email;

        // 验证码
        String code = StrUtil.randNumCode(6);
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from);  // 发送人
        message.setTo(email);
        message.setSentDate(new Date());
        message.setSubject("[White Box]登录邮箱验证");
        message.setText("您本次登录的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露");

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to send the validation code to the target email.");
        }

        redisCache.setCacheObject(redisKey, code, VALIDATION_CODE_EXPIRE, TimeUnit.MINUTES);
    }

    @Override
    public void sendLoginPhoneCode(String phone){

    }

    @Override
    public void sendResetEmailCode(String email){

    }

    @Override
    public void sendResetPhoneCode(String phone){

    }

    @Override
    public void sendRegisterEmailCode(String email) {
        User user = userRepo.selectUserByEmail(email);
        //该邮箱已注册
        ThrowUtil.ifFalse(Objects.isNull(user),
                ErrorCode.ALREADY_EXIST_ERROR, "The email already been registered, please sign up with another email.");

        String redisKey = CacheKey.REGISTER_CODE_EMAIL + email;

        String code = StrUtil.randNumCode(6);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSentDate(new Date());
        message.setSubject("[White Box]注册邮箱验证");
        message.setText("您本次注册的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露");
        javaMailSender.send(message);

        redisCache.setCacheObject(redisKey, code, VALIDATION_CODE_EXPIRE, TimeUnit.MINUTES);
    }

}
