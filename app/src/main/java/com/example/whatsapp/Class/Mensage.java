package com.example.whatsapp.Class;

import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Mensage {
    private String idUser, mensage,image, nameUser;

    public Mensage() {
        this.setNameUser("");
    }

    public void save(String idReciever){
        DatabaseReference firebase = ConfigurationFirebase.getDatabase();
        //User:
        firebase.child("mensages")
                .child(this.idUser)
                .child(idReciever)
                .push()
                .setValue(this);
        //Receiver:
        firebase.child("mensages")
                .child(idReciever)
                .child(this.idUser)
                .push()
                .setValue(this);
    }

    //@Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMensage() {
        return mensage;
    }

    public void setMensage(String mensage) {
        this.mensage = mensage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
