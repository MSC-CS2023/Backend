package uk.gigbookingapp.backend.entity;

public class CurrentId {
    private int id;
    private int usertype;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }
}
