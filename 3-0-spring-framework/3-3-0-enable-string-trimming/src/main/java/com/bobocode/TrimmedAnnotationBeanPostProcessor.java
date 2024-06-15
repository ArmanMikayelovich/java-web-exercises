package com.bobocode;

import com.bobocode.annotation.EnableStringTrimming;
import com.bobocode.annotation.Trimmed;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is processor class implements {@link BeanPostProcessor}, looks for a beans where method parameters are marked with
 * {@link Trimmed} annotation, creates proxy of them, overrides methods and trims all {@link String} arguments marked with
 * {@link Trimmed}. For example if there is a string " Java   " as an input parameter it has to be automatically trimmed to "Java"
 * if parameter is marked with {@link Trimmed} annotation.
 * <p>
 *
 * Note! This bean is not marked as a {@link Component} to avoid automatic scanning, instead it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported to a {@link Configuration} class by annotation
 * {@link EnableStringTrimming}
 */

public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {
//todo: Implement TrimmedAnnotationBeanPostProcessor according to javadoc


    /**
     * 1: look for a beans where method parameters are marked with @Trimmed annotation
     * 2: create proxy of  them
     * 3: override those methods
     * 4; in overriden methods, trim all string arguments with Trimmed annotation
     **/
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        List<Method> methodNamesWithTrimmedAnnotationInParams = getMethodNamesWithTrimmedAnnotationInParams(bean);
        if (!methodNamesWithTrimmedAnnotationInParams.isEmpty()) {


            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setCallback(new ClassWithTrimmedParamsMethodsInvocationHandler(methodNamesWithTrimmedAnnotationInParams));
            bean = enhancer.create();
        }
        return bean;

    }

    private List<Method> getMethodNamesWithTrimmedAnnotationInParams(Object bean) {
        return Stream.of(bean.getClass().getDeclaredMethods())
                .filter(method -> Arrays
                        .stream(method.getParameters())
                        .anyMatch(parameter -> parameter.getType().equals(String.class)
                                && parameter.getAnnotation(Trimmed.class) != null))
                .collect(Collectors.toList());
    }


    private static class ClassWithTrimmedParamsMethodsInvocationHandler implements MethodInterceptor {

        Map<String, boolean[]> methodsWithParamIndexToBeTrimmer = new HashMap<>();

        public ClassWithTrimmedParamsMethodsInvocationHandler(
                List<Method> methods) {

            methods.forEach(method -> {
                Parameter[] parameters = method.getParameters();
                int paramsArrayLength = parameters.length;
                boolean[] isParamShouldBeTrimmed = new boolean[paramsArrayLength];
                for (int i = 0; i < paramsArrayLength; i++) {
                    Parameter parameter = parameters[i];
                    if (parameter.getType().equals(String.class) && (parameter.getAnnotation(Trimmed.class) != null)) {
                        isParamShouldBeTrimmed[i] = true;
                    }
                }
                methodsWithParamIndexToBeTrimmer.put(method.getName(), isParamShouldBeTrimmed);
            });

        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            boolean[] booleans = methodsWithParamIndexToBeTrimmer.get(method.getName());
            if (booleans != null) {
                for (int i = 0; i < booleans.length; i++) {
                    if (booleans[i]) {
                        args[i] = ((String) args[i]).trim();
                    }
                }
            }
            return proxy.invokeSuper(obj, args);
        }
    }
}
