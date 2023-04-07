package com.niceday.tools.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author shuanglian2
 */
@Aspect
@Component
public class NiceLogAspect {

    private long startTime;
    private long endTime;

    /**
     * 带有返回值的日志
     *
     * @param joinPoint
     * @param startTime
     * @param result
     * @param endTime
     * @param annotation
     */
    private static void haveResultLog(JoinPoint joinPoint, long startTime, Object result, long endTime, NiceLog annotation) {
        normalLog(joinPoint, annotation);
        String className = joinPoint.getTarget().getClass().getName();

        System.out.print(" -> ");
        // 判断是否需要打印返回值
        if (annotation.logResult()) {
            // 判断是否需要打印返回值类型
            if (annotation.logResultType()) {
//                System.out.print(result.getClass().getSimpleName() + " ");
                System.out.print(Optional.ofNullable(result).map(res -> res.getClass().getName() + " ").orElse("null" + " "));
            }
            System.out.print("result=" + result);
        }

        // 判断是否需要打印方法执行时间
        if (annotation.logTime()) {
            System.out.print(", time=" + (endTime - startTime) + "ms");
        }
        System.out.println();
    }

    /**
     * 无返回值日志
     *
     * @param joinPoint
     * @param annotation
     */
    private static void normalLog(JoinPoint joinPoint, NiceLog annotation) {
        // 判断是否需要打印所在类名
        if (annotation.logClass()) {
            System.out.print(joinPoint.getSignature().getDeclaringTypeName() + ".");
        }

        // 判断是否需要打印方法名
        if (annotation.logMethod()) {
            System.out.print(joinPoint.getSignature().getName() + "(");
        }

        // 判断是否需要打印参数
        if (annotation.logParams()) {
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                // 判断是否需要打印参数类型
                if (annotation.logParamTypes()) {
                    System.out.print(args[i].getClass().getSimpleName() + " ");
                }
                System.out.print(args[i]);
                if (i < args.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print(")");
        } else {
            System.out.print(")");
        }
    }

    // 定义切点，用于拦截被@CustomAnnotation注解标注的方法或类
//    @Around("@annotation(com.niceday.tools.log.NiceLog) || @within(com.niceday.tools.log.NiceLog)")
//    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        // 获取目标方法所在类上的@CustomAnnotation注解
//        NiceLog annotation = getAnnotation(joinPoint);
//        normalLog(joinPoint, annotation);
//        System.out.println();
//        long startTime = System.currentTimeMillis(); // 记录方法开始执行的时间
//        Object result = joinPoint.proceed(); // 执行目标方法
//        long endTime = System.currentTimeMillis(); // 记录方法结束执行的时间
//        haveResultLog(joinPoint, startTime, result, endTime, annotation);
//
//        return result;
//    }

    // 定义切点，用于拦截被@CustomAnnotation注解标注的方法或类
    @Before("@annotation(com.niceday.tools.log.NiceLog) || @within(com.niceday.tools.log.NiceLog)")
    public void logMethodBefore(JoinPoint joinPoint) {
        // 获取目标方法所在类上的@CustomAnnotation注解
        NiceLog annotation = getAnnotation(joinPoint);
        normalLog(joinPoint, annotation);
        System.out.println();
        // 记录方法开始执行的时间
        startTime = System.currentTimeMillis();
    }

    @AfterReturning(pointcut = "@annotation(com.niceday.tools.log.NiceLog) || @within(com.niceday.tools.log.NiceLog)", returning = "result")
    public void logMethodAfterReturning(JoinPoint joinPoint, Object result) {
        // 获取目标方法所在类上的@CustomAnnotation注解
        NiceLog annotation = getAnnotation(joinPoint);

        // 记录方法结束执行的时间
        endTime = System.currentTimeMillis();
        haveResultLog(joinPoint, startTime, result, endTime, annotation);
    }

    @AfterThrowing(pointcut = "@annotation(com.niceday.tools.log.NiceLog) || @within(com.niceday.tools.log.NiceLog)", throwing = "exception")
    public void logMethodAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        // 获取目标方法所在类上的@CustomAnnotation注解
        NiceLog annotation = getAnnotation(joinPoint);
        // 记录方法结束执行的时间
        endTime = System.currentTimeMillis();
        exceptionLog(joinPoint, startTime, exception, endTime, annotation);
    }

    /**
     * 异常日志
     *
     * @param joinPoint  切点
     * @param startTime  开始时间
     * @param exception  异常
     * @param endTime    结束时间
     * @param annotation 注解
     */
    private void exceptionLog(JoinPoint joinPoint, long startTime, Throwable exception, long endTime, NiceLog annotation) {
        normalLog(joinPoint, annotation);
        String className = joinPoint.getTarget().getClass().getName();

        System.out.print(" -> ");
        System.out.print("exception=" + exception.getMessage());
        // 判断是否需要打印方法执行时间
        if (annotation.logTime()) {
            System.out.print(", time=" + (endTime - startTime) + "ms");
        }
        System.out.println();
    }


    /**
     * 获取注解值
     *
     * @param joinPoint 切点
     * @return
     */
    private NiceLog getAnnotation(JoinPoint joinPoint) {
        NiceLog annotation = (NiceLog) joinPoint.getSignature().getDeclaringType().getAnnotation(NiceLog.class);
        if (annotation == null) {// 如果类上没有注解，则获取方法上的注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            annotation = signature.getMethod().getAnnotation(NiceLog.class);
        }
        return annotation;
    }
}
