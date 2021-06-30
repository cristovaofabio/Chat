package com.example.whatsapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Class.Chat;
import com.example.whatsapp.Class.Group;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    private List<Chat> chats;
    private Context context;

    public ChatsAdapter(List<Chat> chatsList, Context c) {
        this.chats = chatsList;
        this.context = c;
    }

    public List<Chat> getChats(){
        return this.chats;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_list = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts,parent,false);
        return new MyViewHolder(item_list);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.message.setText(chat.getLastMessage());

        if(chat.getIsGroup().equals("true")){
            Group group = chat.getGroup();
            holder.name.setText(group.getName());

            if (group.getPhoto()!=null){
                Uri uri = Uri.parse(group.getPhoto());
                Glide.with(context).load(uri).into(holder.photo);
            }else {
                holder.photo.setImageResource(R.drawable.padrao);
            }

        }else {
            User user = chat.getUserShow();
            if (user!=null){
                holder.name.setText(user.getName());

                if (user.getPhoto()!=null){
                    Uri uri = Uri.parse(user.getPhoto());
                    Glide.with(context).load(uri).into(holder.photo);
                }else {
                    holder.photo.setImageResource(R.drawable.padrao);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView photo;
        TextView name,message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoContact);
            name = itemView.findViewById(R.id.textViewNameContact);
            message = itemView.findViewById(R.id.textViewEmailContact);
        }
    }
}
