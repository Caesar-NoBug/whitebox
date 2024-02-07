package org.caesar.search.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * @TableName search_history
 */
@TableName(value ="search_history")
@Data
@FieldNameConstants
public class SearchHistoryPO implements Serializable {

    /**
     * 搜索历史主键
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户搜索的内容
     */
    private String content;

    /**
     * 搜索时间
     */
    private LocalDateTime searchTime;

    /**
     * 查询数据类型
     */
    private Integer dataSource;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static class CamelFields {
        public static final String id = "id";
        public static final String userId = "user_id";
        public static final String content = "content";
        public static final String searchTime = "search_time";
        public static final String dataSource = "data_source";
    }
}