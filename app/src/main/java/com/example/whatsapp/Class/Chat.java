package com.example.whatsapp.Class;

import android.util.Log;

import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.google.firebase.database.DatabaseReference;

public class Chat {

    private String idReceive, idUser, lastMessage, isGroup;
    private User userShow;
    private Group group;

    public Chat() {
        this.setIsGroup("false");

    }

    public void save(){
        DatabaseReference firebase = ConfigurationFirebase.getDatabase();

        firebase.child("chats")
                .child(this.idUser)
                .child(this.idReceive)
                .setValue(this);
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getIdReceive() {
        return idReceive;
    }

    public void setIdReceive(String idReceive) {
        this.idReceive = idReceive;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMensage) {
        this.lastMessage = lastMensage;
    }

    public User getUserShow() {
        return userShow;
    }

    public void setUserShow(User user) {
        this.userShow = user;
    }
}
