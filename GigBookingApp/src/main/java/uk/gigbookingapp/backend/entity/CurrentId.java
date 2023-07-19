package uk.gigbookingapp.backend.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

public class CurrentId {
    private int id;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
}
