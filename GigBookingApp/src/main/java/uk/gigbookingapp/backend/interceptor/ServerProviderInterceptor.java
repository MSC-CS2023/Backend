package uk.gigbookingapp.backend.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

@Configuration
public class ServerProviderInterceptor implements HandlerInterceptor {
    @Autowired
    private ServiceProviderMapper mapper;

    private CurrentId currentId;

    @Autowired
    public ServerProviderInterceptor(CurrentId currentId) {
        this.currentId = currentId;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization");
        Claims claims;
        try {
            claims = JwtUtils.getClaimsByToken(token);
        } catch (Exception e){
            return Result.error(response, "Token is invalid.");
        }

        Double usertype = (Double) claims.get("usertype");
        if (usertype == null){
            return Result.error(response, "No usertype in the token.");
        }
        if (usertype != (int) UserType.PROVIDER){
            return Result.error(response, "User type in the token is not provider.");
        }
        String uid = claims.getSubject();
        if (uid == null){
            return Result.error(response, "ID does not exist in the token.");
        }
        if (mapper.selectById(uid) == null){
            return Result.error(response, "Invalid ID.");
        }
        this.currentId.setId(Integer.parseInt(uid));

        return true;
    }
}
