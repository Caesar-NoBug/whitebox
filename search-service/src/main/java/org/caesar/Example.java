package org.caesar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Example {

   /* public SearchDto improveSearch(SearchDto searchDto) {

        String text = searchDto.getTerm();
        String type = searchDto.getType();
        HighSearchParam searchParam = searchDto.getSearchParam();

        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest(BwbdType.ES_INDEX);
        // 指定类型
        searchRequest.types(BwbdType.ES_TYPE);
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // 首先构造多关键字查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StringUtils.isNotEmpty(text)) {
            text = QueryParser.escape(text);  // 主要就是这一句把特殊字符都转义,那么lucene就可以识别
            MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders
                    .multiMatchQuery(text, BwbdType.PROPERTY_NUMBERS
                            , BwbdType.PROPERTY_TITLES, BwbdType.PROPERTY_CONTENTS)
                    .field(BwbdType.PROPERTY_NUMBERS, 1f)
                    .field(BwbdType.PROPERTY_TITLES, 0.1f)
                    .field(BwbdType.PROPERTY_CONTENTS, 0.01f)
                    .minimumShouldMatch(BwbdType.MATCH_LEVEL_THREE);
            // 添加条件到布尔查询
            boolQueryBuilder.must(matchQueryBuilder);
        } else {
            if (null == searchDto.getSearchParam() && StringUtils.isEmpty(type)) {
                searchDto.setType(BwbdType.DATA_TYPE_FG);
            }
        }

//        // 通过布尔查询来构造过滤查询
//        boolQueryBuilder.filter(QueryBuilders.matchQuery("economics","L"));
        if (StringUtils.isNotEmpty(type)) {
            boolQueryBuilder.filter(QueryBuilders
                    .matchQuery(BwbdType.PROPERTY_DATA_TYPE, type));
        }

        addFilterProperties(text,searchParam, boolQueryBuilder, searchSourceBuilder);

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = buildFilterFunctionBuilders();

        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(boolQueryBuilder,filterFunctionBuilders)
                .boostMode(CombineFunction.SUM)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM);

        // 将查询条件封装给查询对象
        searchSourceBuilder.query(query);
        if (searchDto.getSize() > 20) {
            searchDto.setSize(20);
        }
        searchSourceBuilder.size(searchDto.getSize());
        searchSourceBuilder.from(searchDto.getPage() - 1);

        // ***********************

        // 高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags(CommonConstraint.LIGHT_TAG_START); // 高亮前缀
        highlightBuilder.postTags(CommonConstraint.LIGHT_TAG_END); // 高亮后缀
        List<HighlightBuilder.Field> fields = highlightBuilder.fields();
        fields.add(new HighlightBuilder
                .Field(BwbdType.PROPERTY_NUMBERS)); // 高亮字段
        fields.add(new HighlightBuilder
                .Field(BwbdType.PROPERTY_TITLES)); // 高亮字段
        fields.add(new HighlightBuilder
                .Field(BwbdType.PROPERTY_CONTENTS).fragmentSize(100000)); // 高亮字段
        // 添加高亮查询条件到搜索源
        searchSourceBuilder.highlighter(highlightBuilder);

        // ***********************

//        // 设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索,向ES发起http请求
        SearchResponse searchResponse = null;
        try (RestHighLevelClient client = new RestHighLevelClient(restClientBuilder)) {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            obtainFgType(searchDto, searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchDto;
    }

    private FunctionScoreQueryBuilder.FilterFunctionBuilder[] buildFilterFunctionBuilders() {
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[3];
        // 时间相关
        ScoreFunctionBuilder<?> dateFieldValueScoreFunction = ScoreFunctionBuilders.fieldValueFactorFunction("contentDate")
                .missing(946656000000d)
                .modifier(FieldValueFactorFunction.Modifier.LN1P).factor(0.1f);
        FunctionScoreQueryBuilder.FilterFunctionBuilder date = new FunctionScoreQueryBuilder.FilterFunctionBuilder(dateFieldValueScoreFunction);
        filterFunctionBuilders[0] = date;
        // 类型相关
        ScoreFunctionBuilder<?> dataTypeFieldValueScoreFunction = ScoreFunctionBuilders.fieldValueFactorFunction("dataTypeRelation")
                .missing(10d)
                .modifier(FieldValueFactorFunction.Modifier.LN1P).factor(1f);
        FunctionScoreQueryBuilder.FilterFunctionBuilder dataType = new FunctionScoreQueryBuilder.FilterFunctionBuilder(dataTypeFieldValueScoreFunction);
        filterFunctionBuilders[1] = dataType;
        // 来源相关
        ScoreFunctionBuilder<?> originFieldValueScoreFunction = ScoreFunctionBuilders.fieldValueFactorFunction("originTypeRelation")
                .missing(10d)
                .modifier(FieldValueFactorFunction.Modifier.LN1P).factor(1f);
        FunctionScoreQueryBuilder.FilterFunctionBuilder origin = new FunctionScoreQueryBuilder.FilterFunctionBuilder(originFieldValueScoreFunction);
        filterFunctionBuilders[2] = origin;
        return filterFunctionBuilders;
    }

    private void addFilterProperties(String text, HighSearchParam searchParam, BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder searchSourceBuilder) {
        if (null != searchParam) {

            if (StringUtils.isNotEmpty(searchParam.getYearStr())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery(BwbdType.PROPERTY_YEARS, searchParam.getYearStr()));
            }
            if (StringUtils.isNotEmpty(searchParam.getReasonName())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("reasonName", searchParam.getReasonName()));
            }
            if (StringUtils.isNotEmpty(searchParam.getCaseType())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("caseType", searchParam.getCaseType()));
            }
            if (StringUtils.isNotEmpty(searchParam.getTrialRoundText())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("trialRoundText", searchParam.getTrialRoundText()));
            }
            if (StringUtils.isNotEmpty(searchParam.getJudgementType())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("judgementType", searchParam.getJudgementType()));
            }
            if (StringUtils.isNotEmpty(searchParam.getAreaCode())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("areaId", searchParam.getAreaCode()));
            }
            if (StringUtils.isNotEmpty(searchParam.getIndustry())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("economics", searchParam.getIndustry()));
            }
            if (StringUtils.isNotEmpty(searchParam.getTaxType())) {
                boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery("stypes", searchParam.getTaxType()));
            }

            // 排序
            // 根据 years 降序排列
            if (BwbdType.ORDER_TYPE_DATE.equals(searchParam.getOrderType())) {
                searchSourceBuilder.sort(new FieldSortBuilder("contentDate").order(SortOrder.DESC));
            }
        }
        // 如果没有检索内容 默认时间排序
        if (StringUtils.isEmpty(text)) {
            searchSourceBuilder.sort(new FieldSortBuilder("contentDate").order(SortOrder.DESC));
        }
        // 根据分数 _score 降序排列 (默认行为)
//        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
    }

    private void obtainFgType(AbstractTxjDto searchDto, SearchResponse searchResponse) {

        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();
        searchDto.setTotal(totalHits);
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        List<BwbdType> bwbdTypes = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            String content = hit.getSourceAsString();//使用ES的java接口将实体类对应的内容转换为json字符串
            BwbdType bwbdType = JSONObject.parseObject(content, BwbdType.class); //生成pojo对象
            // 获取高亮查询的内容。如果存在，则替换原来的name
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField nameField = highlightFields.get(bwbdType.PROPERTY_NUMBERS);
                if (nameField != null) {
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    String numbers = stringBuffer.toString();
                    bwbdType.setNumbers(numbers);
                }

                HighlightField titlesField = highlightFields.get(bwbdType.PROPERTY_TITLES);
                if (titlesField != null) {
                    Text[] fragments = titlesField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    String titles = stringBuffer.toString();
                    bwbdType.setTitles(titles);
                }

                HighlightField contentsField = highlightFields.get(bwbdType.PROPERTY_CONTENTS);
                if (contentsField != null) {
                    Text[] fragments = contentsField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    bwbdType.setContents(stringBuffer.toString());
                }
                // 处理内容
                handleResult(bwbdType);
            }
            bwbdTypes.add(bwbdType);
        }
        searchDto.setRows(bwbdTypes);
    }*/
}
