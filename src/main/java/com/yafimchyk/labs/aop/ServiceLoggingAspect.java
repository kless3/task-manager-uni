package com.yafimchyk.labs.aop;

import com.yafimchyk.labs.exception.DuplicateResourceException;
import com.yafimchyk.labs.exception.InitiatedProblemException;
import com.yafimchyk.labs.exception.LoggingException;
import com.yafimchyk.labs.exception.ResourceNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final String ERROR_EXECUTING_METHOD = "Error executing method!";

    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;

        StopWatch stopWatch = new StopWatch(fullMethodName);

        try {
            stopWatch.start(fullMethodName);
            logger.debug("Выполнение метода: {} с аргументами: {}",
                    fullMethodName, joinPoint.getArgs());

            Object result = joinPoint.proceed();

            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();

            if (executionTime > 1000) {
                logger.warn("Метод {} выполнился за {} мс (превышает порог в 1000 мс)",
                        fullMethodName, executionTime);
            } else if (executionTime > 500) {
                logger.info("Метод {} выполнился за {} мс", fullMethodName, executionTime);
            } else {
                logger.debug("Метод {} выполнился за {} мс", fullMethodName, executionTime);
            }

            return result;

        } catch (Exception e) {
            if (shouldNotWrap(e)) {
                logger.warn("Бизнес-исключение в методе {}: {}", fullMethodName, e.getMessage());
                throw e;
            }

            logger.error("Ошибка при выполнении метода {}: {}", fullMethodName, e.getMessage(), e);
            throw new LoggingException(ERROR_EXECUTING_METHOD);
        }
    }

    private boolean shouldNotWrap(Exception e) {
        return e instanceof ResourceNotFoundException
                || e instanceof DuplicateResourceException
                || e instanceof InitiatedProblemException;
    }
}