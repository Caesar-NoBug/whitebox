package org.caesar.service.impl;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.model.vo.PageVO;
import org.caesar.common.repository.CacheRepository;
import org.caesar.constant.RedisPrefix;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.QuestionSortField;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.ArticleIndex;
import org.caesar.domain.search.vo.QuestionIndex;
import org.caesar.service.SearchService;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ArticleSearchService implements SearchService<ArticleIndex> {

    @Resource
    private ElasticsearchOperations operations;

    @Resource
    private CacheRepository cacheRepo;

    public static final float LIKE_FACTOR = 0.0004f;
    public static final float FAVOR_FACTOR = 0.0008f;
    public static final float VIEW_FACTOR = 0.0001f;

    // 加一个更新时间的权重
    public static final String searchScript = "Math.log(1 + doc['likeNum'].value) * params.likeFactor " +
            "+ Math.log(1 + doc['favorNum'].value) * params.favorFactor + Math.log(1 + doc['viewNum'].value) * params.viewFactor";

    public static final String sortSearchScript = "doc['%s'].value";

    private Map<String, Object> scriptParams;

    @PostConstruct
    public void init() {
        scriptParams = new HashMap<>();
        scriptParams.put("likeFactor", LIKE_FACTOR);
        scriptParams.put("favorFactor", FAVOR_FACTOR);
        scriptParams.put("viewFactor", VIEW_FACTOR);
    }

    //TODO: 添加update字段并在增删改时修改该字段的值
    //TODO: es改成防腐层设计
    @Override
    public PageVO<ArticleIndex> search(String text, int from, int size) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(buildScoreQuery(text))
                .withPageable(PageRequest.of(from, size))
                .withFields(QuestionIndex.RESULT_FIELDS)
                .withSort(SortBuilders.fieldSort(ArticleIndex.FIELD_UPDATE_TIME).order(SortOrder.DESC))
                .build();

        List<SearchHit<ArticleIndex>> searchHits = operations.search(query, ArticleIndex.class).getSearchHits();

        return handleSearchHits(searchHits, text, null);
    }

    @Override
    public PageVO<ArticleIndex> sortSearch(String text, SortField field, int from, int size) {

        ThrowUtil.ifFalse(
                field instanceof QuestionSortField, "排序字段错误：不支持该排序字段");

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(buildSortQuery(text, field))
                .withPageable(PageRequest.of(from, size))
                .withFields(ArticleIndex.RESULT_FIELDS)
                .withSort(SortBuilders.fieldSort(ArticleIndex.FIELD_UPDATE_TIME).order(SortOrder.DESC))
                .build();

        List<SearchHit<ArticleIndex>> searchHits = operations.search(query, ArticleIndex.class).getSearchHits();

        return handleSearchHits(searchHits, text, field);
    }

    @Override
    public String completion(String text) {
        return null;
    }

    @Override
    public boolean insertIndex(List<ArticleIndex> indices) {
        operations.save(indices);
        return true;
    }

    @Override
    public boolean deleteIndex(List<Long> ids) {
        Criteria criteria = new Criteria("id").in(ids);
        operations.delete(criteria);
        return true;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.ARTICLE;
    }

    /**
     * @param text  用户输入的关键词
     * @return      默认规则对应的查询
     */
    private QueryBuilder buildScoreQuery(String text) {

        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.fuzzyQuery(QuestionIndex.FIELD_ALL, text));

        Script script = new Script(ScriptType.INLINE,
                Script.DEFAULT_SCRIPT_LANG, searchScript, scriptParams);

        return QueryBuilders
                .functionScoreQuery(query, ScoreFunctionBuilders.scriptFunction(script))
                .boostMode(CombineFunction.SUM);
    }

    /**
     * @param text 用户关键词
     * @return     匹配关键词的查询
     */
    private QueryBuilder buildSortQuery(String text, SortField field) {

        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.fuzzyQuery(QuestionIndex.FIELD_ALL, text));

        Script script = new Script(ScriptType.INLINE,
                Script.DEFAULT_SCRIPT_LANG, String.format(sortSearchScript, field.getValue()), Collections.emptyMap());

        return QueryBuilders
                .functionScoreQuery(query, ScoreFunctionBuilders.scriptFunction(script))
                .boostMode(CombineFunction.REPLACE);
    }

    private PageVO<ArticleIndex> handleSearchHits(List<SearchHit<ArticleIndex>> searchHits, String text, SortField field) {

        PageVO<ArticleIndex> response = new PageVO<>();

        response.setData(searchHits
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList()));

        response.setTotalSize(searchHits.size());

        String cacheKey;
        String dataSource = getDataSource().getValue();

        if(Objects.isNull(field))
            cacheKey = String.format(RedisPrefix.CACHE_SEARCH_RESULT, dataSource, text);
        else
            cacheKey = String.format(RedisPrefix.CACHE_SORT_SEARCH_RESULT, dataSource, field.getValue(), text);

        int expire = (int) (5 + (Math.random() * 10));
        cacheRepo.setObject(cacheKey, response, expire, TimeUnit.MINUTES);

        return response;
    }

}
