package com.jx.blackmen.annotaion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jx.argo.interceptor.PreInterceptorAnnotation;
import com.jx.blackmen.annotaion.impl.TracePointImpl;

/**
 * 埋点日志
 * @author duqingxiang
 *
 */

@PreInterceptorAnnotation(TracePointImpl.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TracePoint {

}
