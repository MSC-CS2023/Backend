package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gigbookingapp.backend.type.TagsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends User {
    @JsonIgnore
    private String preferenceVector;
    @TableField(exist = false)
    @JsonIgnore
    private List<Double> vector;
    @JsonIgnore
    private Long preferenceTimestamp;

    public List<Double> getVector() {
        return vector;
    }

    public void setVector(List<Double> vector){
        this.vector = vector;
        StringBuilder content = new StringBuilder();
        for (Double d: vector) {
            content.append(String.format("%.5f", d))
                    .append(' ');
        }
        this.preferenceVector = content.toString().trim();
    }


    public void setPreferenceVector(String preferenceVector){
        this.preferenceVector = preferenceVector;
        if (preferenceVector == null || preferenceVector.isEmpty()){
            return;
        }
        String[] strings = preferenceVector.split(" ");
        ArrayList<Double> list = new ArrayList<>();
        for (String s : strings){
            list.add(Double.parseDouble(s));
        }
        setVector(list);
    }
}
