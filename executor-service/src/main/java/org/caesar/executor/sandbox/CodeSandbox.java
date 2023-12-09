package org.caesar.executor.sandbox;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.caesar.common.vo.TaskResult;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.caesar.domain.executor.enums.CodeResultType;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;

import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class CodeSandbox {

    /**
     * @return 沙箱能够处理的语言类型
     */
    public abstract CodeLanguage getLanguage();

    /**
     * 保存用户代码文件
     * @param code 用户代码
     * @return 代码文件目录
     */
    protected abstract TaskResult<String> saveCode(String code);

    /**
     * 编译用户代码文件
     * @param userCodeDir 代码文件目录
     * @return 空
     */
    protected abstract TaskResult<Void> compileCode(String userCodeDir);

    /**
     * 执行用户代码
     * @param userCodeDir 代码文件目录
     * @param inputCase 输入用例
     * @param timeLimit 代码执行时间限制
     * @param memoryLimit 代码执行空间限制
     * @return 代码执行结果
     */
    protected abstract TaskResult<ExecuteCodeResponse> runCode(String userCodeDir, List<String> inputCase, long timeLimit, long memoryLimit);

    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        String code = request.getCode();
        List<String> inputCase = request.getInputCase();
        Long timeLimit = request.getTimeLimit();
        Long memoryLimit = request.getMemoryLimit();

        TaskResult<String> saveCodeResult = saveCode(code);
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

        cleanDir(userCodeDir);
        //执行文件失败
        if (!runCodeResult.isSuccess()) {
            return handleRunCodeError(runCodeResult);
        }

        return handleExecuteCodeSuccess(runCodeResult);
    }

    private static void cleanDir(String dir) {
        boolean flag = FileUtil.del(dir);
        if (!flag) log.error("清空文件目录失败: " + dir);
    }

    private static ExecuteCodeResponse handleSaveCodeError(TaskResult<String> saveCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(false);
        result.setType(Arrays.asList(CodeResultType.SYSTEM_ERROR));
        result.setMessage("系统内部错误，保存文件失败: " + saveCodeResult.getMessage());
        return result;
    }

    private static ExecuteCodeResponse handleCompileCodeError(TaskResult<String> saveCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(false);
        result.setType(Arrays.asList(CodeResultType.COMPILE_ERROR));
        result.setMessage("代码编译错误: " + saveCodeResult.getMessage());
        return result;
    }

    private static ExecuteCodeResponse handleRunCodeError(TaskResult<ExecuteCodeResponse> runCodeResult) {
        ExecuteCodeResponse result = new ExecuteCodeResponse();
        result.setSuccess(false);
        result.setMessage("代码运行错误: " + runCodeResult.getMessage());
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
