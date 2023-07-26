package uk.gigbookingapp.backend.type;

import java.util.HashMap;

public class SortType {

    private HashMap<String, String> map = new HashMap<>();
    private SortType(){
        map.put("time", "timestamp");
        map.put("alphabet", "title");
        map.put("fee", "fee");
    }

    public static String typeToColumn(String type){
        SortType sortType = new SortType();
        return sortType.map.get(type.toLowerCase());
    }

    public static boolean checkAvailable(String type){
        SortType sortType = new SortType();
        return sortType.map.containsKey(type);
    }

}
