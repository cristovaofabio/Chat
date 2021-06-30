package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.whatsapp.Adapter.ContactsAdapter;
import com.example.whatsapp.Adapter.GroupSelectedAdapter;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Toolbar toolbar;
    private RecyclerView recyclerSelectedMembers,recyclerMembers;
    private ContactsAdapter adapter;
    private GroupSelectedAdapter groupAdapter;
    private List<User> listMembers = new ArrayList<>();
    private List<User> listMembersSelected = new ArrayList<>();
    private DatabaseReference usersRef;
    private ValueEventListener valueEventListenerMembers;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        initializeVariables();

        toolbar.setTitle("New Group");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Show back button in toolbar

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembers.setLayoutManager(layoutManager);
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        recyclerSelectedMembers.setHasFixedSize(true);
        recyclerSelectedMembers.setAdapter(groupAdapter);


        recyclerMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User userSelected = listMembers.get(position);

                                //Remove member from list
                                listMembers.remove(userSelected);
                                adapter.notifyDataSetChanged();

                                //Add member in new list
                                listMembersSelected.add(userSelected);
                                groupAdapter.notifyDataSetChanged();
                                updateToolbar();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));

        recyclerSelectedMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerSelectedMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User userSelected = listMembersSelected.get(position);

                                //Remove member from list
                                listMembersSelected.remove(userSelected);
                                groupAdapter.notifyDataSetChanged();

                                //Add member in new list
                                listMembers.add(userSelected);
                                adapter.notifyDataSetChanged();
                                updateToolbar();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCreateGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(GroupActivity.this,RegisterGroupActivity.class);
                i.putExtra("members",(Serializable) listMembersSelected);
                startActivity(i);
            }
        });
    }

    public void initializeVariables(){
        usersRef = ConfigurationFirebase.getDatabase().child("users");
        currentUser = UserFirebase.getUser();
        fab = findViewById(R.id.fabCreateGroup);
        toolbar = findViewById(R.id.toolbarWelcome);
        recyclerSelectedMembers = findViewById(R.id.recyclerSelectedMembers);
        recyclerMembers = findViewById(R.id.recyclerMembers);
        adapter = new ContactsAdapter(listMembers,getApplication());
        groupAdapter = new GroupSelectedAdapter(listMembersSelected,getApplication());
    }

    public void updateToolbar(){
        int totalSelected = listMembersSelected.size();
        int total = listMembers.size() + totalSelected;

        toolbar.setSubtitle(""+totalSelected+" de "+total+" selected");
    }

    @Override
    public void onStart() {
        super.onStart();
        recoveryMembers();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerMembers);
    }

    public void recoveryMembers(){
        valueEventListenerMembers = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMembers.clear();
                listMembersSelected.clear();
                groupAdapter.notifyDataSetChanged();
                for (DataSnapshot datas: snapshot.getChildren() ){

                    User user = datas.getValue(User.class);
                    String emailCurrentUser = currentUser.getEmail();

                    if(!emailCurrentUser.equals(user.getEmail())){
                        listMembers.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
