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

public class GroupSelectedAdapter extends RecyclerView.Adapter<GroupSelectedAdapter.MyViewHolder> {

    private List<User> contactSelected;
    private Context context;

    public GroupSelectedAdapter(List<User> contactsList, Context c) {
        this.contactSelected = contactsList;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_list = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_members_selected,parent,false);
        return new MyViewHolder(item_list);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = contactSelected.get(position);

        holder.name.setText(user.getName());

        if (user.getPhoto()!=null){
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(holder.photo);
        }else {
            holder.photo.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return contactSelected.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView photo;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoMemberSelected);
            name = itemView.findViewById(R.id.textViewNameMemberSelected);
        }
    }
}
