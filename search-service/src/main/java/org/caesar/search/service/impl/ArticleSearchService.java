package org.caesar.search.service.impl;

import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.PageVO;
import org.caesar.domain.search.enums.ArticleSortField;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.search.model.entity.ArticleIndex;
import org.caesar.search.service.SearchService;
import org.caesar.search.util.EsUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleSearchService implements SearchService<ArticleIndexVO> {

    @Resource
    private ElasticsearchOperations operations;

    @Resource
    private ElasticsearchRestTemplate esTemplate;

    public static final float LIKE_FACTOR = 0.04f;
    public static final float FAVOR_FACTOR = 0.08f;
    public static final float VIEW_FACTOR = 0.01f;

    public static final String[] RESULT_FIELDS = new String[]{
            ArticleIndex.Fields.id, ArticleIndex.Fields.title, ArticleIndex.Fields.digest, ArticleIndex.Fields.tag,
            ArticleIndex.Fields.favorNum, ArticleIndex.Fields.viewNum, ArticleIndex.Fields.likeNum, ArticleIndex.Fields.updateAt
    };

    public static final String searchScript = "Math.log(1 + doc['likeNum'].value) * params.likeFactor " +
            "+ Math.log(1 + doc['favorNum'].value) * params.favorFactor + Math.log(1 + doc['viewNum'].value) * params.viewFactor";

    private Map<String, Object> scriptParams;

    @PostConstruct
    public void init() {
        scriptParams = new HashMap<>();
        scriptParams.put("likeFactor", LIKE_FACTOR);
        scriptParams.put("favorFactor", FAVOR_FACTOR);
        scriptParams.put("viewFactor", VIEW_FACTOR);
    }


    @Override
    public PageVO<ArticleIndexVO> search(String text, int from, int size) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(buildScoreQuery(text))
                .withPageable(PageRequest.of(from, size))
                .withFields(RESULT_FIELDS)
                .withSort(SortBuilders.fieldSort(ArticleIndex.Fields.updateAt).order(SortOrder.DESC))
                .build();

        return doSearch(query);
    }

    @Override
    public PageVO<ArticleIndexVO> sortSearch(String text, SortField field, int from, int size) {

        ThrowUtil.ifFalse(
                field instanceof ArticleSortField, "排序字段错误：不支持该排序字段");

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.fuzzyQuery(ArticleIndex.Fields.all, text))
                .withPageable(PageRequest.of(from, size))
                .withFields(RESULT_FIELDS)
                .withSort(SortBuilders.fieldSort(field.getValue()).order(SortOrder.DESC))
                .build();

        return doSearch(query);
    }

    private PageVO<ArticleIndexVO> doSearch(NativeSearchQuery query) {

        SearchHits<ArticleIndex> searchHits = operations.search(query, ArticleIndex.class);

        List<ArticleIndexVO> data = EsUtil.handleSearchHits(searchHits)
                .stream().map(ArticleIndex::toArticleIndexVO).collect(Collectors.toList());

        return new PageVO<>(data, data.size());
    }

    @Override
    public List<String> suggestion(String text, int size) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders
                .completionSuggestion(ArticleIndex.Fields.suggestion)
                .prefix(text)
                .skipDuplicates(true)
                .size(size);

        SuggestBuilder suggestBuilder = new SuggestBuilder()
                .addSuggestion(ArticleIndex.Fields.suggestion, suggestion);

        SearchResponse response = esTemplate
                .suggest(suggestBuilder, IndexCoordinates.of(ArticleIndex.INDEX_NAME));

        return EsUtil.handleSuggestion(response);
    }

    @Override
    public void insertIndex(List<ArticleIndexVO> indices) {

        List<ArticleIndex> indicesDO = indices.stream()
                .map(ArticleIndex::new).collect(Collectors.toList());

        try {
            operations.save(indicesDO);
        } catch (Exception e) {
            LogUtil.error(ErrorCode.SYSTEM_ERROR, "Sync failed indices:" + indicesDO);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to insert index to elasticsearch.");
        }
    }

    @Override
    public void deleteIndex(List<Long> ids) {
        Criteria criteria = new Criteria("id").in(ids);
        try {
            operations.delete(criteria);
        } catch (Exception e) {
            LogUtil.error(ErrorCode.SYSTEM_ERROR, "Delete failed indices" + ids);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to delete index from elasticsearch.");
        }
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.ARTICLE;
    }

    /**
     * @param text 用户输入的关键词
     * @return 默认规则对应的查询
     */
    private QueryBuilder buildScoreQuery(String text) {

        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.fuzzyQuery(ArticleIndex.Fields.all, text));

        Script script = new Script(ScriptType.INLINE,
                Script.DEFAULT_SCRIPT_LANG, searchScript, scriptParams);

        return QueryBuilders
                .functionScoreQuery(query, ScoreFunctionBuilders.scriptFunction(script))
                .boostMode(CombineFunction.SUM);
    }

}
