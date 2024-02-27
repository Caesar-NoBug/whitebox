package org.caesar.search.util;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.util.ListUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EsUtil {

    /**
     * 把searchHits对象转成PageVO对象
     * @param searchHits 响应结果
     * @param <T>        数据类型
     * @return           解析结果
     */
    public static <T> List<T> handleSearchHits(SearchHits<T> searchHits) {
        return ListUtil.convert(searchHits.getSearchHits(), SearchHit::getContent);
    }

    public static List<String> handleSuggestion(SearchResponse response) {
        Suggest suggest = response.getSuggest();
        ThrowUtil.ifNull(suggest, "搜索建议为空");
        List<String> suggestions = new ArrayList<>();

        suggest.forEach(suggestion -> {
            suggestion.getEntries().forEach(entry -> {
                entry.getOptions().forEach(option -> {
                    suggestions.add(option.getText().toString());
                });
            });
        });

        //System.out.println(suggestions);
        return suggestions;
    }
}
