package uk.gigbookingapp.backend.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsType {
    public static final String CLEANING = "cleaning";
    public static final String MAINTENANCE = "maintenance";
    public static final String LAUNDRY = "laundry";
    public static final String LANDSCAPING = "landscaping";
    public static final Map<String, Integer> map = new HashMap<>();
    static {
        map.put(CLEANING, 0);
        map.put(MAINTENANCE, 1);
        map.put(LAUNDRY, 2);
        map.put(LANDSCAPING, 3);
    }

    public static Integer getIndex(String string) {
        return map.get(string);
    }

    public static int getLen(){
        return map.size();
    }

    public static List<Double> initList(List<Double> list){
        list.clear();
        for (int i = 0; i < map.size(); i++) {
            list.add(0.0);
        }
        return list;
    }

}
