package org.caesar.question.constant;

public class CacheKey {

    public static final String QUESTION_INC_ID = "question:incId:";

    public static final String CACHE_QUESTION = "cache:question:";

    public static String cacheQuestion(long questionId) {
        final String prefix = "cache:question:";
        return prefix + questionId;
    }

    // 提交结果
    public static final String SUBMIT_RESULT = "submit:result:%s:%s:%s";

    public static String getSubmitResultKey(long userId, long qId, int submitId) {
        return String.format(SUBMIT_RESULT, userId, qId, submitId);
    }

    public static String questionLikeCount(long questionId) {
        final String prefix = "question:likeCount:";
        return prefix + questionId;
    }

    public static String questionFavorCount(long questionId) {
        final String prefix = "question:favorCount:";
        return prefix + questionId;
    }

    public static String questionSubmitCount(long questionId) {
        final String prefix = "question:submitCount:";
        return prefix + questionId;
    }

    public static String questionPassCount(long questionId) {
        final String prefix = "question:passCount:";
        return prefix + questionId;
    }

}
