package org.caesar.executor.sandbox;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.caesar.common.vo.TaskResult;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.caesar.domain.executor.enums.SubmitCodeResultType;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;

import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class CodeSandbox {

    /**
     * @return 沙箱能够处理的语言类型
     */
    public abstract CodeLanguage getLanguage();

    /**
     * 保存用户代码文件
     *
     * @param code 用户代码
     * @return 容器id
     */
    protected abstract TaskResult<String> saveCode(String code, long memoryLimit);

    /**
     * 编译用户代码文件
     * @param codeDir 代码文件夹
     * @return 空
     */
    protected abstract TaskResult<Void> compileCode(String codeDir);

    /**
     * 执行用户代码
     *
     * @param codeDir     代码文件夹
     * @param inputCase   输入用例
     * @param timeLimit   代码执行时间限制
     * @param memoryLimit 代码执行空间限制
     * @return 代码执行结果
     */
    protected abstract TaskResult<ExecuteCodeResponse> runCode(String codeDir, List<String> inputCase, long timeLimit, long memoryLimit);

    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {

        String code = request.getCode();
        List<String> inputCase = request.getInputCase();
        Long timeLimit = request.getTimeLimit();
        Long memoryLimit = request.getMemoryLimit();

        TaskResult<String> saveCodeResult = saveCode(code, memoryLimit);
        //保存文件失败
        if (!saveCodeResult.isSuccess()) {
            return handleSaveCodeError(saveCodeResult);
        }

        //源代码
        String userCodeDir = saveCodeResult.getData();
        TaskResult<Void> compileCodeResult = compileCode(userCodeDir);

        //编译文件失败
        if (!compileCodeResult.isSuccess()) {
            cleanDir(userCodeDir);
            return handleCompileCodeError(saveCodeResult);
        }

        TaskResult<ExecuteCodeResponse> runCodeResult = runCode(userCodeDir, inputCase, timeLimit, memoryLimit);

        // TODO: 删除镜像改成方法
        cleanDir(userCodeDir);
        //执行文件失败
        if (!runCodeResult.isSuccess()) {
            return handleRunCodeError(runCodeResult);
        }

        return handleExecuteCodeSuccess(runCodeResult);
    }

    private static void cleanDir(String dir) {
        boolean flag = FileUtil.del(dir);
        if (!flag) log.error("Fail to clear code file directory: " + dir);
    }

    private static ExecuteCodeResponse handleSaveCodeError(TaskResult<String> saveCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(false);
        result.setType(Collections.singletonList(SubmitCodeResultType.SYSTEM_ERROR));
        result.setMessage("Error occurred when saving code file: " + saveCodeResult.getMessage());
        return result;
    }

    private static ExecuteCodeResponse handleCompileCodeError(TaskResult<String> saveCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(false);
        result.setType(Collections.singletonList(SubmitCodeResultType.COMPILE_ERROR));
        result.setMessage("Error occurred when compiling code: " + saveCodeResult.getMessage());
        return result;
    }

    private static ExecuteCodeResponse handleRunCodeError(TaskResult<ExecuteCodeResponse> runCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(false);
        result.setMessage("Error occurred when executing code: " + runCodeResult.getMessage());
        return result;
    }

    private static ExecuteCodeResponse handleExecuteCodeSuccess(TaskResult<ExecuteCodeResponse> runCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(true);
        result.setResult(runCodeResult.getData().getResult());
        result.setMessage(runCodeResult.getData().getMessage());
        result.setType(runCodeResult.getData().getType());
        result.setTime(runCodeResult.getData().getTime());
        return result;
    }

}
