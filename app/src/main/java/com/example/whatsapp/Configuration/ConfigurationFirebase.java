package com.example.whatsapp.Configuration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigurationFirebase {

    private static DatabaseReference databaseRef;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //return database:
    public static DatabaseReference getDatabase(){
        if (databaseRef==null){
            databaseRef = FirebaseDatabase.getInstance().getReference();
        }
        return databaseRef;
    }

    //return user:
    public static FirebaseAuth getAuth(){
        if (auth==null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getStorage(){
        if (storage==null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

}
