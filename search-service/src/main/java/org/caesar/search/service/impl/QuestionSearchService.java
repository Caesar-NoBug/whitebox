package org.caesar.search.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.Logger;
import org.caesar.common.str.StrUtil;
import org.caesar.common.util.ListUtil;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.QuestionSortField;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.search.model.MsQuestionStruct;
import org.caesar.search.model.entity.QuestionIndex;
import org.caesar.domain.search.vo.PageVO;
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

@Service
public class QuestionSearchService implements SearchService<QuestionIndexVO> {

    @Resource
    private ElasticsearchOperations operations;

    @Resource
    private ElasticsearchRestTemplate esTemplate;

    @Resource
    private MsQuestionStruct questionStruct;

    public static final float LIKE_FACTOR = 0.04f;
    public static final float FAVOR_FACTOR = 0.08f;
    public static final float SUBMIT_FACTOR = 0.01f;

    public static final String searchScript = "Math.log(1 + doc['likeNum'].value) * params.likeFactor " +
            "+ Math.log(1 + doc['favorNum'].value) * params.favorFactor + Math.log(1 + doc['submitNum'].value) * params.submitFactor";

    public static final String[] RESULT_FIELDS = new String[]{
            QuestionIndex.Fields.id, QuestionIndex.Fields.title, QuestionIndex.Fields.tag,
            QuestionIndex.Fields.favorNum, QuestionIndex.Fields.submitNum, QuestionIndex.Fields.likeNum,
            QuestionIndex.Fields.difficulty, QuestionIndex.Fields.passNum
    };

    private Map<String, Object> scriptParams;

    @PostConstruct
    public void init() {
        scriptParams = new HashMap<>();
        scriptParams.put("likeFactor", LIKE_FACTOR);
        scriptParams.put("favorFactor", FAVOR_FACTOR);
        scriptParams.put("submitFactor", SUBMIT_FACTOR);
    }

    @Logger(value = "/searchQuestion", args = true, result = true)
    @Override
    public PageVO<QuestionIndexVO> search(String text, int from, int size) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(buildDefaultScoreQuery(text))
                .withPageable(PageRequest.of(from, size))
                .withFields(RESULT_FIELDS)
                .build();

        return doSearch(query);
    }

    @Logger(value = "/searchQuestion", args = true, result = true)
    @Override
    public PageVO<QuestionIndexVO> sortSearch(String text, SortField field, int from, int size) {

        ThrowUtil.ifFalse(
                field instanceof QuestionSortField, "Invalid sort field");

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(buildQuery(text))
                .withPageable(PageRequest.of(from, size))
                .withSort(SortBuilders.fieldSort(field.getValue()).order(SortOrder.DESC))
                .withFields(RESULT_FIELDS)
                .build();

        return doSearch(query);
    }

    private PageVO<QuestionIndexVO> doSearch(NativeSearchQuery query) {

        SearchHits<QuestionIndex> searchHits = operations.search(query, QuestionIndex.class);

        List<QuestionIndexVO> data = ListUtil.convert(EsUtil.handleSearchHits(searchHits),
                QuestionIndex::toQuestionIndexVO);

        if(CollectionUtil.isEmpty(data)) return new PageVO<>(data, 0);

        return new PageVO<>(data, data.size());
    }

    @Override
    public List<String> suggestion(String text, int size) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders
                .completionSuggestion(QuestionIndex.Fields.suggestion)
                .prefix(text)
                .skipDuplicates(true)
                .size(size);

        SuggestBuilder suggestBuilder = new SuggestBuilder()
                .addSuggestion(QuestionIndex.Fields.suggestion, suggestion);

        SearchResponse response = esTemplate
                .suggest(suggestBuilder, IndexCoordinates.of(QuestionIndex.INDEX_NAME));

        return EsUtil.handleSuggestion(response);
    }

    @Override
    public void insertIndex(List<QuestionIndexVO> indices) {

        List<QuestionIndex> indicesDO = ListUtil.convert(indices, QuestionIndex::new);
        operations.save(indicesDO);
    }

    @Override
    public void deleteIndex(List<Long> ids) {
        Criteria criteria = new Criteria("id").in(ids);
        operations.delete(criteria);
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.QUESTION;
    }

    /**
     * @param text 用户输入的关键词
     * @return 默认规则对应的查询
     */
    private QueryBuilder buildDefaultScoreQuery(String text) {

        QueryBuilder query = buildQuery(text);

        Script script = new Script(ScriptType.INLINE,
                Script.DEFAULT_SCRIPT_LANG, searchScript, scriptParams);

        return QueryBuilders
                .functionScoreQuery(query, ScoreFunctionBuilders.scriptFunction(script))
                .boostMode(CombineFunction.SUM);
    }

    private QueryBuilder buildQuery(String text) {
        return StrUtil.isBlank(text)
                ? QueryBuilders.matchAllQuery()
                : QueryBuilders.fuzzyQuery(QuestionIndex.Fields.all, text);
    }
}
