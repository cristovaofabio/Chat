package com.example.whatsapp.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.Activitiy.ChatActivity;
import com.example.whatsapp.Activitiy.GroupActivity;
import com.example.whatsapp.Adapter.ContactsAdapter;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerContactList;
    private ContactsAdapter adapter;
    private ArrayList<User> contactsList = new ArrayList<>();
    private DatabaseReference usersRef;
    private ValueEventListener valueEventListenerContacts;
    private FirebaseUser currentUser;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerContactList = view.findViewById(R.id.recyclerViewContacts);
        usersRef = ConfigurationFirebase.getDatabase().child("users");
        currentUser = UserFirebase.getUser();

        //Adapter configuration
        adapter = new ContactsAdapter(contactsList,getActivity());

        //RecyclerView configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerContactList.setLayoutManager(layoutManager);
        recyclerContactList.setHasFixedSize(true);
        recyclerContactList.setAdapter(adapter);

        recyclerContactList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerContactList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<User> updatedListUser = adapter.getUsers();

                                User selectedUser = updatedListUser.get(position);
                                boolean header = selectedUser.getEmail().isEmpty();

                                if (header){
                                    Intent i = new Intent(getContext(), GroupActivity.class);
                                    startActivity(i);
                                }else {
                                    Intent i = new Intent(getContext(), ChatActivity.class);
                                    i.putExtra("contactChat",selectedUser);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recoveryContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContacts);
    }

    public void recoveryContacts(){

        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear();

                //Create the function NEW GROUP:
                User itemGroup = new User();
                itemGroup.setName("New group");
                itemGroup.setEmail("");
                contactsList.add(itemGroup);

                for (DataSnapshot datas: snapshot.getChildren() ){

                    User user = datas.getValue(User.class);
                    String emailCurrentUser = currentUser.getEmail();

                    if(!emailCurrentUser.equals(user.getEmail())){
                        contactsList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void searchContacts(String text){
        List<User> searchListContacts = new ArrayList<>();
        for (User user: contactsList){
            String name = user.getName().toLowerCase();
            if (name.contains(text)){
                searchListContacts.add(user);
            }

        }
        adapter = new ContactsAdapter(searchListContacts,getActivity());
        recyclerContactList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void reloadContacts(){
        adapter = new ContactsAdapter(contactsList,getActivity());
        recyclerContactList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}