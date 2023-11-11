package org.caesar.sandbox.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import org.caesar.common.TaskResult;
import org.caesar.domain.constant.enums.CodeLanguage;
import org.caesar.domain.constant.enums.CodeResultType;
import org.caesar.domain.request.executor.ExecuteCodeRequest;
import org.caesar.domain.response.executor.ExecuteCodeResponse;
import org.caesar.sandbox.CodeSandbox;
import org.caesar.common.util.IOUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class JavaCodeSandbox extends CodeSandbox {

    //TODO: 这些常量改成参数化配置的形式
    private static String basicCodePath;
    private static final String GLOBAL_CODE_FILE_NAME = "Main.java";
    private static final String GLOBAL_COMPILED_FILE_NAME = "Main.class";
    private static final String GLOBAL_CODE_DIR_NAME = "javaCode";
    private static final String COMPILE_CODE_COMMAND = "javac -encoding utf-8 %s";
    private static final String EXECUTE_CODE_COMMAND = "java -Dfile.encoding=UTF-8 -cp %s Main";

    private JavaCodeSandbox() {
        basicCodePath = System.getProperty("user.dir") + File.separator + GLOBAL_CODE_DIR_NAME;
        if (!FileUtil.exist(basicCodePath)) {
            FileUtil.mkdir(basicCodePath);
        }
    }

    private static class InnerHolder {
        private static final JavaCodeSandbox SINGLE_TON = new JavaCodeSandbox();
    }

    public static JavaCodeSandbox getInstance() {
        return InnerHolder.SINGLE_TON;
    }

    public static void main(String[] args) {
        JavaCodeSandbox sandbox = JavaCodeSandbox.getInstance();

        ExecuteCodeRequest request = new ExecuteCodeRequest();

        request.setLanguage(CodeLanguage.JAVA);
        request.setInputCase(Arrays.asList("1 2\n", "3 4\n"));
        String code = ResourceUtil.readStr("testCode/Main.java", StandardCharsets.UTF_8);
        request.setCode(code);
        ExecuteCodeResponse executeCodeResult = sandbox.executeCode(request);
        System.out.println(executeCodeResult);
    }

    @Override
    public CodeLanguage getLanguage() {
        return CodeLanguage.JAVA;
    }

    @Override
    protected TaskResult<String> saveCode(String code) {

        //当前请求的代码目录
        String userCodeDirPath = basicCodePath + File.separator + UUID.randomUUID().toString();

        try {
            FileUtil.writeString(code, userCodeDirPath + File.separator + GLOBAL_CODE_FILE_NAME, StandardCharsets.UTF_8).getAbsolutePath();
        } catch (IORuntimeException e) {
            return TaskResult.err("保存代码文件错误:" + e.toString());
        }

        return TaskResult.ok(userCodeDirPath);
    }

    @Override
    protected TaskResult<Void> compileCode(String userCodeDir) {

        String codeFile = userCodeDir + File.separator + GLOBAL_CODE_FILE_NAME;

        try {
            Process compileProcess = Runtime.getRuntime().exec(String.format(COMPILE_CODE_COMMAND, codeFile));
            int value = compileProcess.waitFor();

            if (value != 0) {
                String message = IOUtil.readAll(compileProcess.getErrorStream());
                return TaskResult.err("编译失败: " + message);
            }

            compileProcess.destroy();

        } catch (IOException e) {
            return TaskResult.err("读取代码文件失败: " + e.getMessage());
        } catch (InterruptedException e) {
            return TaskResult.err("编译失败：" + e.getMessage());
        }

        return TaskResult.ok(null);
    }

    @Override
    protected TaskResult<ExecuteCodeResponse> runCode(String userCodeDir, List<String> inputCase, long timeLimit, long memoryLimit) {

        int size = inputCase.size();
        ExecuteCodeResponse executeResponse = new ExecuteCodeResponse();
        List<CodeResultType> types = new ArrayList<>(size);
        List<String> results = new ArrayList<>(size);
        List<Long> times = new ArrayList<>(size);
        boolean isSuccess = true;
        executeResponse.setType(types);
        executeResponse.setResult(results);
        executeResponse.setTime(times);

        try {

            StopWatch stopWatch = new StopWatch();
            for (String string : inputCase) {

                try {
                    //TODO: 这里要确保input是以\n结尾，否则程序可能会卡死
                    String input = string;
                    Process process = Runtime.getRuntime().exec(String.format(EXECUTE_CODE_COMMAND, userCodeDir));

                    OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
                    writer.write(input);
                    writer.flush();
                    stopWatch.start();
                    int exit = process.waitFor();
                    stopWatch.stop();

                    Long time = stopWatch.getLastTaskTimeMillis();
                    String result = IOUtil.readAll(process.getInputStream());
                    CodeResultType type;
                    if (exit == 0)
                        type = CodeResultType.TEMPORARY_ACCEPTED;
                    else {
                        String message = IOUtil.readAll(process.getErrorStream());
                        type = CodeResultType.RUNTIME_ERROR;
                        if (isSuccess) {
                            executeResponse.setMessage(message);
                        }
                        isSuccess = false;
                    }

                    types.add(type);
                    results.add(result);
                    times.add(time);
                    process.destroy();

                } catch (InterruptedException e) {
                    //TODO: 处理超时，超内存，非法操作等异常情况
                    throw new RuntimeException(e);
                }

            }
            //executeResponse.setSuccess(isSuccess);

        } catch (IOException e) {
            return TaskResult.err("读取代码文件失败: " + e.getMessage());
        }

        return TaskResult.ok(executeResponse);
    }
}
