package org.caesar.runner;

import org.caesar.domain.constant.StrConstant;
import org.caesar.domain.search.vo.QuestionIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

@Component
public class QuestionRunner implements ApplicationRunner {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public void run(ApplicationArguments args){
        IndexCoordinates coordinates = IndexCoordinates.of(StrConstant.QUESTION_INDEX);
        IndexOperations indexOperations = elasticsearchOperations.indexOps(coordinates);
        if(!indexOperations.exists()) {
            //TODO: 如果未创建索引则批量从对应服务中批量拉取数据并同步到es中
            indexOperations.create();
            indexOperations.refresh();
            indexOperations.putMapping(QuestionIndex.class);
            indexOperations.refresh();

            elasticsearchOperations.save(new QuestionIndex(0L, null, "这个是标题", "这个是内容", new String[]{"字符串", "数组", "正则表达式"}, 0, 0, 1));
            elasticsearchOperations.save(new QuestionIndex(6L, null, "这个是一个标题", "这个是好多内容", new String[]{"数组", "表达式"}, 0, 6, 0));
            elasticsearchOperations.save(new QuestionIndex(9L, null, "这个是一个大标题", "这个是非常好的内容", new String[]{"数组", "表达式"}, 5, 6, 0));
        }

    }

}
