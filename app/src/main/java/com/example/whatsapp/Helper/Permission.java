package com.example.whatsapp.Helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validatePermissions(String[] permissions, Activity activity, int requesCode){

        if (Build.VERSION.SDK_INT >= 23){
            List<String> permissionsList = new ArrayList<>();

            for (String permision:permissions){
               Boolean havePermission = ContextCompat.checkSelfPermission(activity,permision)== PackageManager.PERMISSION_GRANTED;
                if (!havePermission){
                    permissionsList.add(permision);
                }
            }
            if (permissionsList.isEmpty()){
                return true;
            }
            String [] newPermissions = new String[permissionsList.size()];
            permissionsList.toArray(newPermissions);
            ActivityCompat.requestPermissions(activity,newPermissions,requesCode);
        }

        return true;
    }
}
