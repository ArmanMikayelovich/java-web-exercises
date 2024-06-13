package com.bobocode;

import com.bobocode.annotation.EnableStringTrimming;
import org.springframework.context.annotation.Bean;

public class StringTrimmingConfiguration {

    @Bean
    public TrimmedAnnotationBeanPostProcessor get() {
        return new TrimmedAnnotationBeanPostProcessor();
    }
}
