package org.caesar.common.model.vo;

import lombok.Data;

@Data
public class PageRequest {
    private String keyword;
    private int from;
    private int size;
}
