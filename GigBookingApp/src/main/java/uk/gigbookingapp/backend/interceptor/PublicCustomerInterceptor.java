package uk.gigbookingapp.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.type.UserType;

@Configuration
public class PublicCustomerInterceptor implements HandlerInterceptor {

    private CurrentId currentId;
    @Autowired
    public PublicCustomerInterceptor(CurrentId currentId) {
        this.currentId = currentId;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        this.currentId.setUsertype(UserType.CUSTOMER);
        return true;
    }

}
