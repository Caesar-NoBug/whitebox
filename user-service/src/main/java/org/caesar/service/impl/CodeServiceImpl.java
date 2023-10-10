package org.caesar.service.impl;

import org.caesar.constant.RedisPrefix;
import org.caesar.model.dto.AuthUser;
import org.caesar.model.vo.Response;
import org.caesar.service.CodeService;
import org.caesar.service.UserService;
import org.caesar.common.util.RedisCache;
import org.caesar.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CodeServiceImpl implements CodeService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Response sendLoginEmailCode(String email){

        AuthUser authUser = userService.selectAuthUserByEmail(email);
        //查无此人
        if(Objects.isNull(authUser))
            return Response.error("该邮箱尚未注册");

        String redisKey = RedisPrefix.LOGIN_CODE_EMAIL + email;

        String code = StrUtil.randNumCode(6);
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from);  // 发送人
        message.setTo(email);
        message.setSentDate(new Date());
        message.setSubject("[White Box]登录邮箱验证");
        message.setText("您本次登录的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露");
        javaMailSender.send(message);

        redisCache.setCacheObject(redisKey, code, 5, TimeUnit.MINUTES);

        return Response.ok(null, "成功发送验证码");
    }

    @Override
    public Response sendLoginPhoneCode(String phone){
        return null;
    }

    @Override
    public Response sendResetEmailCode(String email){
        return null;
    }

    @Override
    public Response sendResetPhoneCode(String phone){
        return null;
    }

    @Override
    public Response sendRegisterEmailCode(String email) {
        AuthUser authUser = userService.selectAuthUserByEmail(email);
        //该邮箱已注册
        if(!Objects.isNull(authUser))
            return Response.error("该邮箱已被注册，请重新注册");

        String redisKey = RedisPrefix.REGISTER_CODE_EMAIL + email;

        String code = StrUtil.randNumCode(6);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSentDate(new Date());
        message.setSubject("[White Box]注册邮箱验证");
        message.setText("您本次注册的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露");
        javaMailSender.send(message);

        redisCache.setCacheObject(redisKey, code, 5, TimeUnit.MINUTES);

        return Response.ok(null, "成功发送验证码");
    }
}
