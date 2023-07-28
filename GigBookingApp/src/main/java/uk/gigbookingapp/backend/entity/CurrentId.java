package uk.gigbookingapp.backend.entity;

public class CurrentId {
    private Long id;
    private int usertype;

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return this.id;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }
}
