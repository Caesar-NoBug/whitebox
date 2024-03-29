package org.caesar.search.model;

import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.search.model.entity.ArticleIndex;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsArticleStruct{

    ArticleIndex VOtoDO(ArticleIndexVO articleIndexVO);

    ArticleIndexVO DOtoVO(ArticleIndex articleIndex);
}
