package org.caesar.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import org.caesar.common.constant.enums.SortField;
import org.caesar.common.model.vo.PageResponse;
import org.caesar.common.model.vo.QuestionIndex;
import org.caesar.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QueryService<QuestionIndex> {

    @Autowired
    private ElasticsearchOperations operations;
    //TODO: 同步mysql数据到es（批量同步，一批一批同步，但是不要一次性全部同步）
    //TODO: 按某个字段的值排序

    public static final double THUMB_WEIGHT = 0.0004;
    public static final double FAVOR_WEIGHT = 0.0008;
    public static final double SUBMIT_WEIGHT = 0.0001;
    public static final double MAX_BOOST = 0.4;

    //TODO: 添加update字段并在增删改时修改该字段的值
    @Override
    public PageResponse<QuestionIndex> query(String keyword, int from, int size) {
        List<FunctionScore> functions = new ArrayList<>();

        functions.add(new FunctionScore.Builder().fieldValueFactor(f -> f.field("favorNum").factor(FAVOR_WEIGHT).modifier(FieldValueFactorModifier.Log1p)).build());
        functions.add(new FunctionScore.Builder().fieldValueFactor(f -> f.field("submitNum").factor(SUBMIT_WEIGHT).modifier(FieldValueFactorModifier.Log1p)).build());
        functions.add(new FunctionScore.Builder().fieldValueFactor(f -> f.field("thumbNum").factor(THUMB_WEIGHT).modifier(FieldValueFactorModifier.Log1p)).build());

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(
                        q -> q.functionScore(
                                fn -> fn.query(
                                                f -> f.match(m -> m.field("all").query(keyword))
                                        )
                                        .functions(functions)
                                        .boostMode(FunctionBoostMode.Sum)

                        )
                )
                .withPageable(PageRequest.of(from, size))
                .build();

        SearchHits<QuestionIndex> searchHits = operations.search(nativeQuery, QuestionIndex.class);


        PageResponse<QuestionIndex> response = new PageResponse<>();
        response.setData(searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList()));
        response.setTotalSize(searchHits.getSearchHits().size());

        return response;
    }

    @Override
    public PageResponse<QuestionIndex> sortQuery(String keyword, SortField field, int from, int size) {
        return null;
    }

    @Override
    public boolean insertIndex(List<QuestionIndex> indexes) {
        return false;
    }

    @Override
    public boolean deleteIndex(List<Integer> ids) {
        return false;
    }

}
