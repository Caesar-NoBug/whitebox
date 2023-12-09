package org.caesar.constant;

import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("(?s)```(.*?)```");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("(//.*?\\n)|(#.*\\n)|(/\\*.*?\\*/|/\\*(.|\\n)*?\\*/)");

}
