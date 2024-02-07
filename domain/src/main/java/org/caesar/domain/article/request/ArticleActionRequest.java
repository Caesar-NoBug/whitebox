package org.caesar.domain.article.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotNull;

@Data
public class ArticleActionRequest {

    @NotNull
    private Integer id;

    @Range(min = -1, max = 1)
    private Integer mark;

    @NotNull
    private Boolean isFavor;
}
