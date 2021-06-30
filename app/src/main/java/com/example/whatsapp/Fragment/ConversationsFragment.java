package com.example.whatsapp.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp.Activitiy.ChatActivity;
import com.example.whatsapp.Adapter.ChatsAdapter;
import com.example.whatsapp.Class.Chat;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.RecyclerItemClickListener;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment {

    private RecyclerView recyclerConversations;
    private List<Chat> listChats = new ArrayList<>();
    private ChatsAdapter adapter;
    private User user;
    private DatabaseReference databaseRef;
    private DatabaseReference chatsRef;
    private ChildEventListener childEventListenerChat;

    public ConversationsFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        recyclerConversations = view.findViewById(R.id.recyclerListChats);

        user = UserFirebase.getUserLogOn();
        databaseRef = ConfigurationFirebase.getDatabase();
        adapter = new ChatsAdapter(listChats,getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerConversations.setLayoutManager(layoutManager);
        recyclerConversations.setHasFixedSize(true);
        recyclerConversations.setAdapter(adapter);

        chatsRef = databaseRef
                .child("chats")
                .child(user.getId());

        recyclerConversations.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerConversations,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                List<Chat> updatedListChat = adapter.getChats();
                                Chat selectedChat = updatedListChat.get(position);

                                if (selectedChat.getIsGroup().equals("true")){
                                    Intent i = new Intent(getContext(), ChatActivity.class);
                                    i.putExtra("contactGroup",selectedChat.getGroup());
                                    startActivity(i);

                                }else {
                                    Intent i = new Intent(getContext(), ChatActivity.class);
                                    i.putExtra("contactChat",selectedChat.getUserShow());
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recoveryChats();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsRef.removeEventListener(childEventListenerChat);
    }
    public void searchChats(String text){
        List<Chat> searchListChats = new ArrayList<>();
        for (Chat chat: listChats){

            if(chat.getIsGroup().equals("true")){
                String name = chat.getGroup().getName();
                //String lastMessage = chat.getLastMessage().toLowerCase();
                if (name.contains(text)){
                    searchListChats.add(chat);
                }
            }else{
                String name = chat.getUserShow().getName().toLowerCase();
                String lastMessage = chat.getLastMessage().toLowerCase();
                if (name.contains(text) || lastMessage.contains(text)){
                    searchListChats.add(chat);
                }
            }
        }
        adapter = new ChatsAdapter(searchListChats,getActivity());
        recyclerConversations.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void reloadChats(){
        adapter = new ChatsAdapter(listChats,getActivity());
        recyclerConversations.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recoveryChats(){
        listChats.clear();
        childEventListenerChat = chatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chat chat = snapshot.getValue(Chat.class);
                listChats.add(chat);
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
