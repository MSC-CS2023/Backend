package uk.gigbookingapp.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage {
    private boolean isSystem;
    private Long recipientId;
    private Integer userType;
    private Object message;
}
