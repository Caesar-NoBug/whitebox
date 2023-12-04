package org.caesar.client;

import org.caesar.model.vo.ChatResponse;
import org.caesar.model.vo.ChatRequest;

//AIGC接口
public interface ChatClient {
    ChatResponse chat(ChatRequest request);
}
