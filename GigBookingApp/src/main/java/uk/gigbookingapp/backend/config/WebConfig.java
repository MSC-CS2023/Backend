package uk.gigbookingapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gigbookingapp.backend.interceptor.TokenInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Without this part start with @Bean, the TokenInterceptor cannot use mappers.
    @Bean
    public TokenInterceptor getLoginInterceptor(){
        return new TokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Set the path will be intercepted.
        // Now the TokenInterceptor() will intercept all url ending with "/user/**"
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/user/**");
        // If we need treat the interceptor as a Bean, the 'getLoginInterceptor()'
        // can be changed as 'new TokenInterceptor()'
    }
}
