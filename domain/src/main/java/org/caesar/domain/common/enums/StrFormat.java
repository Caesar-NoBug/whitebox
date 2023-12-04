package org.caesar.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public enum StrFormat {
    DEFAULT(Pattern.compile(".*")),
    EMAIL(Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")),
    PHONE(Pattern.compile("^1\\d{10}$")),
    NUM_CODE(Pattern.compile("^\\d+$")),
    STR_CODE(Pattern.compile("^[a-zA-Z0-9]+$"));

    private final Pattern pattern;
}
