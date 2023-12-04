package org.caesar.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {

    /**
     * 发送者角色
     */
    private Role role;

    /**
     * 发送的内容
     */
    private String content;

    public static Message preset(String content) {
        return new Message(Role.system, content);
    }

    public static Message prompt(String content) {
        return new Message(Role.user, content);
    }

    public static Message reply(String content) {
        return new Message(Role.assistant, content);
    }

}
