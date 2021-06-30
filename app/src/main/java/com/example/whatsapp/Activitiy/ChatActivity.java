package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Adapter.MensagesAdapter;
import com.example.whatsapp.Class.Chat;
import com.example.whatsapp.Class.Group;
import com.example.whatsapp.Class.Mensage;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.Base64Custon;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView imageContact;
    private TextView textNameContact;
    private User user, receiverUser;
    private Group group;
    private String groupID;
    private EditText mensage;
    private FloatingActionButton fab;
    private String idReceiver;
    private RecyclerView recyclerMensage;
    private MensagesAdapter adapter;
    private List<Mensage> mensages = new ArrayList<>();
    private DatabaseReference databaseRef;
    private DatabaseReference mensagesRef;
    private ChildEventListener childEventListenerMensage;
    private String idMember;

    private ImageView camera;
    private static final int GALLERY_SELECTION = 200;
    private byte[] imageDates;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        groupID="1";

        initializeVariables();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Show back button in toolbar

        //Recovery user datas:
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){

            if (bundle.containsKey("contactGroup")){

                group = (Group) bundle.getSerializable("contactGroup");//group
                idReceiver = group.getId();
                textNameContact.setText(group.getName());
                String photo = group.getPhoto();
                groupID = group.getId();

                if (photo!=null){
                    Uri url = Uri.parse(photo);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(imageContact);
                }else {
                    imageContact.setImageResource(R.drawable.padrao);
                }

            }else {

                receiverUser = (User) bundle.getSerializable("contactChat");//person
                idReceiver = Base64Custon.encodeBase64(receiverUser.getEmail());
                textNameContact.setText(receiverUser.getName());
                String photo = receiverUser.getPhoto();

                if (photo!=null){
                    Uri url = Uri.parse(photo);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(imageContact);
                }else {
                    imageContact.setImageResource(R.drawable.padrao);
                }

            }
        }

        //RecyclerView configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensage.setLayoutManager(layoutManager);
        recyclerMensage.setHasFixedSize(true);
        recyclerMensage.setAdapter(adapter);
        mensagesRef = databaseRef
                .child("mensages")
                .child(user.getId())
                .child(idReceiver);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mensag = mensage.getText().toString();

                if (!mensag.isEmpty()){

                    Mensage msg = new Mensage();
                    msg.setMensage(mensag);

                    if(receiverUser!=null){
                        msg.setIdUser(user.getId());
                        msg.save(idReceiver);

                        saveChat(msg,false);

                    }else {
                        //From User to Group:
                        msg.setIdUser(user.getId());
                        msg.setNameUser(user.getName());
                        msg.save(group.getId());

                        //From Group to users:
                        msg.setIdUser(group.getId());
                        for (User member: group.getMembers()){
                            idMember = Base64Custon.encodeBase64(member.getEmail()); //User ID
                            if(!idMember.equals(user.getId())){
                                msg.setNameUser(user.getName());
                                saveChat(msg,true);
                                msg.save(idMember); //Send message from group
                            }
                        }
                    }
                }else {
                    Toast.makeText(ChatActivity.this,
                            "It's necessary to write a mensage",
                            Toast.LENGTH_SHORT).show();
                }
                mensage.setText("");
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
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

    private void saveChat(Mensage msg, boolean isGroup){
        Chat chatReceive = new Chat();

        if(isGroup){
            chatReceive.setIdReceive(idMember);
            chatReceive.setIdUser(group.getId());
            chatReceive.setIsGroup("true");
            chatReceive.setLastMessage(msg.getMensage());
            chatReceive.setGroup(group);

            chatReceive.save();

        }else{
            chatReceive.setIdReceive(idReceiver);
            chatReceive.setIdUser(user.getId());
            chatReceive.setLastMessage(msg.getMensage());
            chatReceive.setIsGroup("false");
            chatReceive.setUserShow(receiverUser);

            chatReceive.save();

            Chat chatReceive2 = new Chat();

            chatReceive2.setIdReceive(user.getId());
            chatReceive2.setIdUser(idReceiver);
            chatReceive2.setLastMessage(msg.getMensage());
            chatReceive2.setIsGroup("false");
            chatReceive2.setUserShow(user);

            chatReceive2.save();

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

                    //Recovery dates from Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    imageDates = baos.toByteArray();

                    String nameImage = UUID.randomUUID().toString();

                    //Save image in Firebase:
                    final StorageReference imageRef = storage.child("images")
                            .child("photos")
                            .child(user.getId())
                            .child(nameImage+".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageDates);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this,
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
                                    Mensage msn = new Mensage();

                                    if(receiverUser!=null){
                                        msn.setIdUser(user.getId());
                                        msn.setMensage("image.jpeg");
                                        msn.setImage(urlImage.toString());
                                        msn.save(idReceiver);

                                    }else {
                                        //From User to Group:
                                        msn.setIdUser(user.getId());
                                        msn.setNameUser(user.getName());
                                        msn.setMensage("image.jpeg");
                                        msn.setImage(urlImage.toString());
                                        msn.save(group.getId());

                                        //From Group to users:
                                        msn.setIdUser(group.getId());
                                        for (User member: group.getMembers()){
                                            idMember = Base64Custon.encodeBase64(member.getEmail()); //User ID
                                            if(!idMember.equals(user.getId())){
                                                msn.setNameUser(user.getName());
                                                msn.setMensage("image.jpeg");
                                                msn.setImage(urlImage.toString());
                                                msn.save(idMember); //Send message from group
                                            }
                                        }
                                    }


                                }
                            });

                            Toast.makeText(ChatActivity.this,
                                    "Success to send image",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void initializeVariables(){

        toolbar = findViewById(R.id.toolbarChat);
        imageContact = findViewById(R.id.circlePhotoChat);
        textNameContact = findViewById(R.id.textViewNameChat);
        mensage = findViewById(R.id.editTextMensage);
        fab = findViewById(R.id.fabSend);
        user = UserFirebase.getUserLogOn();
        recyclerMensage = findViewById(R.id.recyclerChat);
        adapter = new MensagesAdapter(mensages,getApplicationContext());
        databaseRef = ConfigurationFirebase.getDatabase();
        camera = findViewById(R.id.imageCamera);
        storage = ConfigurationFirebase.getStorage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoveryMensage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagesRef.removeEventListener(childEventListenerMensage);
    }

    private void recoveryMensage(){
        mensages.clear();
        childEventListenerMensage = mensagesRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensage mensage = snapshot.getValue(Mensage.class);
                mensages.add(mensage);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
