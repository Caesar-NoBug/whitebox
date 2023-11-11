package org.caesar.domain.vo.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "user_index", createIndex = false)
public class UserIndex implements Index{

    /**
     * 主键，用户唯一标识
     */
    @Id
    private Long id;

    /**
     * 用户名，不多于20个字符
     */
    @Field(type = FieldType.Text, store = true)
    private String username;

}
