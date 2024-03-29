package org.caesar.executor.sandbox;

import org.caesar.domain.executor.enums.CodeLanguage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CodeSandboxFactory implements ApplicationContextAware {

    private final Map<CodeLanguage, CodeSandbox> sandboxMap = new ConcurrentHashMap<>();

    public CodeSandbox getCodeSandbox(CodeLanguage language) {
        return sandboxMap.get(language);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, CodeSandbox> tempMap = applicationContext.getBeansOfType(CodeSandbox.class);
        tempMap.values().forEach(codeSandbox -> sandboxMap.put(codeSandbox.getLanguage(), codeSandbox));
    }

}
