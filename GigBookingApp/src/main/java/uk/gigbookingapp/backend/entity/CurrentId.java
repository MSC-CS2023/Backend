package uk.gigbookingapp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gigbookingapp.backend.type.UserType;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentId {
    private Long id;
    private int usertype;

    public void setUsertype(int usertype){
        this.usertype = usertype;
    }
    public void setUsertype(String usertype) throws Exception{
        this.usertype = switch (usertype) {
            case "customer" -> UserType.CUSTOMER;
            case "service_provider", "service provider" -> UserType.PROVIDER;
            default -> throw new Exception();
        };
    }

    @Override
    public String toString() {
        return "CurrentId{" +
                "id=" + id +
                ", usertype=" + usertype +
                '}';
    }
}
