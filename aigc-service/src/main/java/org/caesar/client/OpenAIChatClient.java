package org.caesar.client;

import com.alibaba.fastjson.JSON;
import org.caesar.model.vo.ChatRequest;
import org.caesar.model.vo.ChatResponse;
import org.caesar.model.vo.OpenAIChatResponse;
import org.caesar.common.exception.BusinessException;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public class OpenAIChatClient implements ChatClient {

    private final RestTemplate template = new RestTemplate();

    //请求头
    private final HttpHeaders headers = new HttpHeaders();

    //请求方式
    private final HttpMethod method = HttpMethod.POST;

    //请求密钥
    private String apiKey = "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";

    //模型请求地址
    private String url = "https://api.openai.com/v1/chat/completions";

    //TODO: 参数配置化
    @PostConstruct
    public void init() {
        //配置代理
        SimpleClientHttpRequestFactory reqFactory = new SimpleClientHttpRequestFactory();
        reqFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)));
        template.setRequestFactory(reqFactory);

        // 设置请求类型、apikey
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
    }

    @Override
    public ChatResponse chat(ChatRequest request) {

        // 设置请求体
        String requestBody = JSON.toJSONString(request);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 发送请求
        ResponseEntity<String> entity = template.exchange(url, method, requestEntity, String.class);

        //  解析响应
        if (entity.getStatusCode() != HttpStatus.OK)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to request the model api, the error code is " + entity.getStatusCode());

        OpenAIChatResponse response;

        try {
            response = JSON.parseObject(entity.getBody(), OpenAIChatResponse.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "fail to transfer the response object" + e.toString());
        }

        return response;
    }


}
