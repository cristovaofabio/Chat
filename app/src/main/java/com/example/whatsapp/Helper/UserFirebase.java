package com.example.whatsapp.Helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static String getIdUser(){
        String emailUser, idUser;
        FirebaseAuth auth = ConfigurationFirebase.getAuth();
        emailUser = auth.getCurrentUser().getEmail();
        idUser = Base64Custon.encodeBase64(emailUser);

        return idUser;
    }

    public static FirebaseUser getUser(){
        FirebaseAuth auth = ConfigurationFirebase.getAuth();

        return auth.getCurrentUser();
    }

    public static boolean updateUserName(String name){

        try {
            FirebaseUser user = getUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Profile","Error to update profile name");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserImage(Uri url){

        try {
            FirebaseUser user = getUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Profile","Error to update profile image");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static User getUserLogOn(){
        FirebaseUser firebaseUser = getUser();
        User user = new User();

        String emailUser = firebaseUser.getEmail();
        String idUser = Base64Custon.encodeBase64(emailUser);

        user.setId(idUser);
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());

        if (firebaseUser.getPhotoUrl()==null){
            user.setPhoto("");
        }else {
            user.setPhoto(firebaseUser.getPhotoUrl().toString());
        }
        return user;
    }

}
