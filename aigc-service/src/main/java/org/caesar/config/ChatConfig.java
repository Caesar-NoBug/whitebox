package org.caesar.config;

import lombok.Data;

@Data
public class ChatConfig {

    private String preset;

    private String prompt;

    private String reply;

    private String separator;
}
