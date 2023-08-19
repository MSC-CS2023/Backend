package uk.gigbookingapp.backend.type;

import java.util.List;

public enum TagsType {
//    public static final String CLEANING = "cleaning";
//    public static final String MAINTENANCE = "maintenance";
//    public static final String LAUNDRY = "laundry";
//    public static final String LANDSCAPING = "landscaping";
    CLEANING,
    MAINTENANCE,
    LAUNDRY,
    LANDSCAPING,
    SUM_NUM;



//    public static final Map<String, Integer> map = new HashMap<>();
//    static {
//        map.put(CLEANING, 0);
//        map.put(MAINTENANCE, 1);
//        map.put(LAUNDRY, 2);
//        map.put(LANDSCAPING, 3);
//    }

//    public static Integer getIndex(String string) {
//        return map.get(string);
//    }

    public static int getLen(){
        return SUM_NUM.ordinal();
    }

    public static List<Double> initList(List<Double> list){
        list.clear();
        for (int i = 0; i < SUM_NUM.ordinal(); i++) {
            list.add(0.0);
        }
        return list;
    }

}
