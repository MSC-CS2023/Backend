package uk.gigbookingapp.backend.ws;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.SessionMapper;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.JwtUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndpoint {

    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    ServiceProviderMapper providerMapper;
    @Autowired
    SessionMapper sessionMapper;

    private static final Map<String, Session> onlineUsers = new ConcurrentHashMap<String, Session>();
    private Session session;
    private HttpSession httpSession;
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String token = (String) session.getUserProperties().get("Authorization");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("");
        }
        CurrentId currentId = parseToken(token);
        onlineUsers.put(currentId.toString(), session);
//        String message = MessageUtils.getMessage(true, null, getFriends());
//        broadcastAllUsers(message);
//        String message = new ObjectMapper().writeValueAsString();
    }

//    private ArrayList<ChattedPeople> getUnread(CurrentId currentId) throws Exception {
//        ArrayList<ChattedPeople> result = new ArrayList<>();
//        QueryWrapper<SessionObj> wrapper = new QueryWrapper<>();
//        wrapper.eq("recipient_id", currentId.getId())
//                .eq("recipient_type", currentId.getUsertype())
//                .eq("unread", true)
//                .orderByDesc("timestamp");
//
//        List<SessionObj> sessionObjs = sessionMapper.selectList(wrapper);
//        HashMap<String, ArrayList<SessionObj>> sessionHashMap = new HashMap<>();
//
////        for (SessionObj sessionObj: sessionObjs){
////            CurrentId user = new CurrentId();
////            user.setId(sessionObj.getSenderId());
////            user.setUsertype(sessionObj.getSenderType());
////            if ()
////
////        }
//    }

    private Set getFriends() {
        Set<String> set = onlineUsers.keySet();
        return set;
    }

    private void broadcastAllUsers(String message){
        try {
            Set<Map.Entry<String, Session>> entries = onlineUsers.entrySet();
            for (Map.Entry<String, Session> entry : entries) {
                entry.getValue().getAsyncRemote().sendText(message);
            }
        } catch (Exception ignore){}
    }

    @OnMessage
    public void onMessage(String message){
        this.session = session;
    }
    @OnClose
    public void onClose(Session session){
        String token = (String) session.getUserProperties().get("Authorization");
        CurrentId currentId = parseToken(token);
        onlineUsers.remove(currentId);
    }

    private CurrentId parseToken(String token){
        Claims claims;
        try {
            claims = JwtUtils.getClaimsByToken(token);
        } catch (Exception e){
            throw new RuntimeException("Invalid token");
        }

        Integer usertype = (Integer) claims.get("usertype");
        if (usertype == null){
            throw new RuntimeException("No usertype in the token.");
        }
        String uid = claims.getSubject();
        if (uid == null){
            throw new RuntimeException("ID does not exist in the token.");
        }
        BaseMapper mapper;
        if (usertype.equals(UserType.CUSTOMER)) {
            mapper = customerMapper;
        } else if (usertype.equals(UserType.PROVIDER)) {
            mapper = providerMapper;
        } else {
            throw new RuntimeException("User type is wrong.");
        }

        if (mapper.selectById(uid) == null){
            throw new RuntimeException("Invalid ID.");
        }
        CurrentId currentId = new CurrentId();
        currentId.setId(Long.parseLong(uid));
        currentId.setUsertype(usertype);
        return currentId;
    }
}
