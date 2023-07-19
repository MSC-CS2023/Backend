package uk.gigbookingapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.interceptor.CustomerInterceptor;

@Configuration
public class CustomerConfig implements WebMvcConfigurer {
    @Bean
    public CurrentId getCurrentId(){
        return new CurrentId();
    }

    @Bean
    public CustomerInterceptor getCustomerInterceptor(){
        return new CustomerInterceptor(getCurrentId());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Set the path will be intercepted.
        // Now the TokenInterceptor() will intercept all url ending with "/user/**"
        registry.addInterceptor(getCustomerInterceptor()).addPathPatterns("/customer/**");
        // If we need treat the interceptor as a Bean, the 'getLoginInterceptor()'
        // can be changed as 'new TokenInterceptor()'
    }
}
