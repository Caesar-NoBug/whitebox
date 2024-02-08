package org.caesar.service;


import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;

public interface AnalyseTextService {

    //  文本分析（文章和评论）
    AnalyseTextResponse analyseText(AnalyseTextRequest request);
}
