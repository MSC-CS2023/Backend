package uk.gigbookingapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.interceptor.CustomerInterceptor;
import uk.gigbookingapp.backend.interceptor.ServerProviderInterceptor;

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
    @Bean
    public ServerProviderInterceptor getServerProviderInterceptor(){
        return new ServerProviderInterceptor(getCurrentId());
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Set the path will be intercepted.
        // Now the getCustomerInterceptor() will intercept all url ending with "/customer/**"
        registry.addInterceptor(getCustomerInterceptor()).addPathPatterns("/customer/**");
        registry.addInterceptor(getServerProviderInterceptor()).addPathPatterns("/service_provider/**");
        // If we need treat the interceptor as a Bean, the 'getLoginInterceptor()'
        // can be changed as 'new getCustomerInterceptor()'
    }
}
