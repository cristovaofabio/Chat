package com.example.whatsapp.Class;

import android.util.Log;

import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.Base64Custon;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String id,name,photo;
    private List<User> members;

    public Group() {
        DatabaseReference database = ConfigurationFirebase.getDatabase();
        DatabaseReference databaseRef = database.child("group");

        //Generate and save a ID:
        String idFirebase = databaseRef.push().getKey();
        setId(idFirebase);
    }

    public void save(){
        DatabaseReference firebase = ConfigurationFirebase.getDatabase();
        firebase.child("groups")
                .child(getId())
                .setValue(this);

        for (User user: getMembers()){

            String idUser = Base64Custon.encodeBase64(user.getEmail());
            String idReceive = getId();

            Chat chat = new Chat();
            chat.setIdUser(idUser);
            chat.setIdReceive(idReceive);
            chat.setLastMessage("");
            chat.setIsGroup("true");
            chat.setGroup(this);

            chat.save();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
