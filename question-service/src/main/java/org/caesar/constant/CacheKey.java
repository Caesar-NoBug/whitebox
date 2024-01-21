package org.caesar.constant;

public class CacheKey {

    public static final String QUESTION_INC_ID = "question:incId:";

    public static final String CACHE_QUESTION = "cache:question:";
    // 提交结果（userId, qId, submitId）
    public static final String SUBMIT_RESULT = "submit:result:%s:%s:%s";

    public static String getSubmitResultKey(long userId, long qId, int submitId) {
        return String.format(SUBMIT_RESULT, userId, qId, submitId);
    }

}
