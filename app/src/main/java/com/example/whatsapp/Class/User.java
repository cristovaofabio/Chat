package com.example.whatsapp.Class;

import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String id,name,password,email,photo;

    public User() {
    }

    public void save(){
        DatabaseReference firebase = ConfigurationFirebase.getDatabase();
        firebase.child("users")
                .child(this.id)
                .setValue(this);
    }

    public void update(){
        String idUser = UserFirebase.getIdUser();
        DatabaseReference database = ConfigurationFirebase.getDatabase();

        DatabaseReference usersRef = database.child("users")
                .child(idUser);

        Map<String,Object> userValues = convertMap();

        usersRef.updateChildren(userValues);
    }

    @Exclude
    public Map<String,Object> convertMap(){
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("email",getEmail());
        userMap.put("name",getName());
        userMap.put("photo",getPhoto());

        return userMap;
    }

    @Exclude
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

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
