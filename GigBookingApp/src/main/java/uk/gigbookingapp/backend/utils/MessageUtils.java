package uk.gigbookingapp.backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gigbookingapp.backend.entity.CurrentId;

public class MessageUtils {
    public static String getMessage(boolean isSystemMessage, CurrentId currentId, Object message){
        try {
            ResultMessage result = new ResultMessage();
            result.setSystem(isSystemMessage);
            result.setMessage(message);
            if (currentId != null){
                result.setRecipientId(currentId.getId());
                result.setUserType(currentId.getUsertype());
            }
            // To JSON String
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(result);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }
}