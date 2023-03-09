package com.niceday.tools.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NiceLog {

    boolean logClass() default true; // 是否打印类名

    boolean logMethod() default true; // 是否打印方法名

    boolean logParams() default true; // 是否打印方法入参

    boolean logResult() default true; // 是否打印方法出参

    boolean logResultType() default true; // 是否打印方法出参类型

    boolean logParamTypes() default true; // 是否打印方法入参类型

    boolean logTime() default true; // 是否打印方法执行时间

}
