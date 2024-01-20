package org.caesar.common.idempotent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.util.MethodUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Aspect
public class IdempotentAspect {

    public static final String REQUEST_ID_PATTERN = "reqId:%s:%s:%s";

    public static final String IDEMPOTENT_LUA = "local status = redis.call('EXISTS',KEYS[1]);\n" +
            "if status == 0\n" +
            "then\n" +
            "   redis.call('SETEX',KEYS[1],ARGV[1],1)\n" +
            "   return \"2\" \n" +
            "else \n" +
            "  return redis.call('GET',KEYS[1])\n" +
            "end";

    // 缓存表达式
    public static final Map<Method, Expression> CACHE_EXPR_MAP = new ConcurrentHashMap<>();

    @Resource
    private CacheRepository cacheRepo;

    @Around("@annotation(org.caesar.common.idempotent.Idempotent)")
    public Object handleIdempotent(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        String key = getRequestId(method, idempotent, joinPoint.getArgs());

        String status = tryLock(key, idempotent.expire());

        switch (status) {
            // 正在处理中
            case "1": {
                throw new BusinessException(ErrorCode.DUPLICATE_REQUEST, "The current request is being processed.");
            }
            // 尚未开始处理，开始执行
            case "2": {
                Object result;
                try {
                    result = joinPoint.proceed();
                } catch (Throwable e) {
                    removeLock(key);
                    throw e;
                }

                setLockComplete(key);
                return result;
            }
            // 已处理成功
            case "3": {
                return Response.ok(null, idempotent.msg());
            }
            // 非法status
            default: {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "illegal result status of idempotent lua eval.");
            }
        }
    }

    // 尝试获取锁
    private String tryLock(String key, int expire) {
        return cacheRepo.eval(IDEMPOTENT_LUA, Collections.singletonList(key), new Object[] {String.valueOf(expire)});
    }

    // 设置锁为完成状态
    private void setLockComplete(String key) {
        cacheRepo.setObject(key, "3");
    }

    private void removeLock(String key) {
        cacheRepo.deleteObject(key);
    }

    private String getRequestId(Method method, Idempotent idempotent, Object[] args) {

        String prefix = idempotent.value();

        Expression expression = getExpression(method, idempotent.reqId());

        List<String> paramNames = MethodUtil.getParamNames(method);

        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < paramNames.size(); i++) {
            context.setVariable(paramNames.get(i), args[i]);
        }

        Long id;

        try {
            id = expression.getValue(context, Long.class);
        } catch (EvaluationException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "fail to evaluate the expression(Illegal expr or requestId type)");
        }

        return String.format(REQUEST_ID_PATTERN, prefix, ContextHolder.getUserIdRequired(), id);
    }

    private Expression getExpression(Method method, String key) {
        return CACHE_EXPR_MAP.computeIfAbsent(method, m -> {
            SpelExpressionParser parser = new SpelExpressionParser();
            return parser.parseExpression(key);
        });
    }

}
