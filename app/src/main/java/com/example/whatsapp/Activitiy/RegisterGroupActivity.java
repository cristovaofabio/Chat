package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Adapter.GroupSelectedAdapter;
import com.example.whatsapp.Class.Group;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterGroupActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Toolbar toolbar;
    private List<User> listMembersSelected = new ArrayList<>();
    private TextView participants;
    private ProgressDialog progressDialog;
    private CircleImageView photoGroup;
    private EditText nameGroup;
    private RecyclerView recyclerParticipantsGroup;
    private GroupSelectedAdapter groupSelectedAdapter;
    private static final int GALLERY_SELECTION = 200;
    private byte[] imageDates;
    private StorageReference storageReference;
    private Group group;
    private String urPhotoGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);

        initializeVariables();

        toolbar.setTitle("New group");
        toolbar.setSubtitle("Difine a name");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Show back button in toolbar

        if(getIntent().getExtras()!=null){
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            listMembersSelected.addAll(members);
            participants.setText("Participants: "+listMembersSelected.size());
        }

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        recyclerParticipantsGroup.setLayoutManager(layoutManagerHorizontal);
        recyclerParticipantsGroup.setHasFixedSize(true);
        recyclerParticipantsGroup.setAdapter(groupSelectedAdapter);

        photoGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(i,GALLERY_SELECTION);
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressLoading();
                saveImageFirebase();
            }
        });
    }
    public void initializeVariables(){
        fab = findViewById(R.id.fabRegisterGroup);
        toolbar = findViewById(R.id.toolbarWelcome);
        participants = findViewById(R.id.textViewParticipantsGroup);
        photoGroup = findViewById(R.id.imageViewPhotoGroup);
        nameGroup = findViewById(R.id.editTextNameGroup);
        storageReference = ConfigurationFirebase.getStorage();
        groupSelectedAdapter = new GroupSelectedAdapter(listMembersSelected,getApplication());
        recyclerParticipantsGroup = findViewById(R.id.recyclerParticipantsGroup);
        group = new Group();
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
                    photoGroup.setImageBitmap(bitmap);

                    //Recovery dates from Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    imageDates = baos.toByteArray();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveGroup(){
        listMembersSelected.add(UserFirebase.getUserLogOn());
        group.setMembers(listMembersSelected);
        group.setName(nameGroup.getText().toString());
        group.setPhoto(urPhotoGroup);
        group.save();

        progressDialog.dismiss();//Finish progress bar

        Toast.makeText(RegisterGroupActivity.this,
                "Success to save informations",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    public void saveImageFirebase(){

        final StorageReference imageRef = storageReference
                .child("images")
                .child("group")
                .child(group.getId()+".jpeg");

        UploadTask uploadTask = imageRef.putBytes(imageDates);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterGroupActivity.this,
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
                        urPhotoGroup = urlImage.toString();
                        saveGroup();
                    }
                });
            }
        });

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
