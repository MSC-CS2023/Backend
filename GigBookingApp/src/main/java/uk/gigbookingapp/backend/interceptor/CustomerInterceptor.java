package uk.gigbookingapp.backend.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.utils.JwtUtils;

@Configuration
public class CustomerInterceptor implements HandlerInterceptor {
    @Autowired
    private CustomerMapper mapper;

    private CurrentId currentId;

    @Autowired
    public CustomerInterceptor(CurrentId currentId) {
        this.currentId = currentId;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization");
        Claims claims;
        try {
            claims = JwtUtils.getClatimsByToken(token);
        } catch (Exception e){
            return false;
        }
        String uid = claims.getSubject();
        if (mapper.selectById(uid) == null){
            return false;
        }
        this.currentId.setId(Integer.parseInt(uid));
        System.out.println(currentId);
        return true;
    }
}
