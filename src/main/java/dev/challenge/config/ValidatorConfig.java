package dev.challenge.config;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

@Configuration
public class ValidatorConfig {

    @Bean
    public LocalValidatorFactoryBean validator(AutowireCapableBeanFactory factory) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();

        bean.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(factory));

        return bean;
    }
}
