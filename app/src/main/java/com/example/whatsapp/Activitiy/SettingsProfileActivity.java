package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.Base64Custon;
import com.example.whatsapp.Helper.Permission;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText name;
    private CircleImageView image;
    private ProgressDialog progressDialog;
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //Manifest.permission.CAMERA
    };
    private static final int GALLERY_SELECTION = 200;
    private StorageReference storageReference;
    //private String idUser;
    private FirebaseUser firebaseUser;
    private User userLogOn;
    private Uri url;
    private byte[] imageDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_profile);

        toolbar = findViewById(R.id.toolbarWelcome);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Show back button in toolbar

        initializeVariables();

        Permission.validatePermissions(permissions,this,1);

        checkImage();

        name.setText(firebaseUser.getDisplayName());
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(i,GALLERY_SELECTION);
                }
            }
        });
    }

    public void initializeVariables(){

        name = findViewById(R.id.editTextName);
        image = findViewById(R.id.profile_image);
        storageReference = ConfigurationFirebase.getStorage();
        //idUser = UserFirebase.getIdUser();
        firebaseUser = UserFirebase.getUser();
        userLogOn = UserFirebase.getUserLogOn();
        url = firebaseUser.getPhotoUrl();

    }

    public void checkImage(){
        if (url!=null){
            Glide.with(SettingsProfileActivity.this)
                    .load(url)
                    .into(image);
        }else {
            image.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Bitmap bitmap =null;
            try {

                switch (requestCode){
                    case GALLERY_SELECTION:
                        Uri localSelectedImage = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),localSelectedImage);
                        break;
                }
                if (bitmap!=null){
                    image.setImageBitmap(bitmap);

                    //Recovery dates from Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    imageDates = baos.toByteArray();

                    //Save image in Firebase:

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveImageFirebase(){

        /*
        final StorageReference imageRef = storageReference
                .child("images")
                .child("profile")
                .child(idUser+".jpeg");*/

        final StorageReference imageRef = storageReference
                .child("images")
                .child("profile")
                .child(userLogOn.getId()+".jpeg");

        UploadTask uploadTask = imageRef.putBytes(imageDates);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsProfileActivity.this,
                        "Error to upload image",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri urlImage = task.getResult();
                        updateUserImage(urlImage);
                    }
                });

                progressDialog.dismiss();//Finish progress bar

                Toast.makeText(SettingsProfileActivity.this,
                        "Success to save informations",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateUserImage(Uri urlImage){
        boolean result = UserFirebase.updateUserImage(urlImage);
        if(result){
            userLogOn.setPhoto(urlImage.toString());
            userLogOn.update();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissionResult: grantResults){
            if(permissionResult == PackageManager.PERMISSION_DENIED){
                alertValidationPermission();
            }
        }
    }

    private void alertValidationPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Denied permissions!");
        builder.setMessage("It's necessary accept the conditions to use the app");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void saveInformations(View view){
        String nameProfile = name.getText().toString();
        boolean result = UserFirebase.updateUserName(nameProfile);

        if (result){
            progressLoading();
            userLogOn.setName(nameProfile);
            saveImageFirebase();
            userLogOn.update();
        }
    }

    public void progressLoading() {
        //Show progress dialog during list image loading
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Saving datas...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
