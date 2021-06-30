package com.example.whatsapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Class.Mensage;
import com.example.whatsapp.Class.User;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;

import java.util.List;

public class MensagesAdapter extends RecyclerView.Adapter<MensagesAdapter.MyViewHolder> {

    private List<Mensage> mensages;
    private Context context;
    private static final int TYPE_USER =0;
    private static final int TYPE_RECIEVE =1;

    public MensagesAdapter(List<Mensage> list, Context c) {
        this.mensages = list;
        this.context = c;

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;

        if (viewType==TYPE_USER){
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_mensage_user,parent,false);

        }else if (viewType==TYPE_RECIEVE){
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_mensage_recieve,parent,false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Mensage mensage = mensages.get(position);
        String msg = mensage.getMensage();
        String image = mensage.getImage();
        String name = mensage.getNameUser();

        if (!name.isEmpty()){
            holder.name.setText(name);
        }else {
            holder.name.setVisibility(View.GONE);
        }

        if (image!=null){
            Uri url = Uri.parse(image);
            Glide.with(context).load(url).into(holder.image);
            //Hide text
            holder.mensag.setVisibility(View.GONE);

        }else {

            holder.mensag.setText(msg);
            //Hide image
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensage mensage = mensages.get(position);
        User user = UserFirebase.getUserLogOn();
        String idUser = user.getId();
        if (idUser.equals(mensage.getIdUser())){
            return TYPE_USER;
        }
        return TYPE_RECIEVE;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mensag;
        TextView name;
        ImageView image;

        public MyViewHolder(View itemView){
            super(itemView);

            mensag = itemView.findViewById(R.id.textMensageChat);
            name = itemView.findViewById(R.id.textNameShow);
            image = itemView.findViewById(R.id.imageMensagePhoto);
        }
    }
}
