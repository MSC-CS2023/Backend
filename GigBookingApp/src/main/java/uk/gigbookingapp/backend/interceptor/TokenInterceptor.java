package uk.gigbookingapp.backend.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gigbookingapp.backend.mapper.UserMapper;
import uk.gigbookingapp.backend.utils.JwtUtils;

//@Component // The '@Autowired' will error without this annotation
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

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
        if (userMapper.selectById(uid) == null){
            return false;
        }
        //request.setAttribute("uid", uid);
        return true;
    }

}
