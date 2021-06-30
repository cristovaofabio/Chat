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
import com.example.whatsapp.Class.User;
import com.example.whatsapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private List<User> contacts;
    private Context context;

    public ContactsAdapter(List<User> contactsList, Context c) {
        this.contacts = contactsList;
        this.context = c;
    }
    public List<User> getUsers(){
        return this.contacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_list = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts,parent,false);
        return new MyViewHolder(item_list);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = contacts.get(position);
        boolean header = user.getEmail().isEmpty();

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        if (user.getPhoto()!=null){
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(holder.photo);
        }else {
            if (header){
                holder.photo.setImageResource(R.drawable.icone_grupo);
                holder.email.setVisibility(View.GONE);
            }else {
                holder.photo.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView photo;
        TextView name,email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoContact);
            name = itemView.findViewById(R.id.textViewNameContact);
            email = itemView.findViewById(R.id.textViewEmailContact);
        }
    }
}
