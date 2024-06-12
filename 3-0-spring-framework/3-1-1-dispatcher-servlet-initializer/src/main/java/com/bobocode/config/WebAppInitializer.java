package com.bobocode.config;

import com.bobocode.util.ExerciseNotCompletedException;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.util.Arrays;

/**
 * This class is used to configure DispatcherServlet and links it with application config classes
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        Class[] array = {RootConfig.class};
        return (Class<?>[]) array;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        Class[] array = {WebConfig.class};
        return (Class<?>[]) array;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
