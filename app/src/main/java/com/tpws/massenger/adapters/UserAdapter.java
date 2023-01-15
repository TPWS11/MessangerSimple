package com.tpws.massenger.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tpws.massenger.R;
import com.tpws.massenger.activities.ChatActivity;
import com.tpws.massenger.activities.HomeActivity;
import com.tpws.massenger.models.ModelUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context homeActivity;
    ArrayList<ModelUser> users;

    public UserAdapter(Context homeActivity, ArrayList<ModelUser> users) {
        this.homeActivity = homeActivity;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelUser modelUser = users.get(position);

        holder.username.setText(modelUser.getName());
        holder.status.setText(modelUser.getStatus());
        Picasso.get().load(modelUser.getImageUri()).into(holder.profil);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity, ChatActivity.class);
                intent.putExtra("name", modelUser.getName());
                intent.putExtra("image", modelUser.getImageUri());
                intent.putExtra("uid", modelUser.getUid());
                homeActivity.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profil;
        TextView username, status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profil = itemView.findViewById(R.id.ci_profil_user);
            username = itemView.findViewById(R.id.tv_user);
            status = itemView.findViewById(R.id.tv_status_user);
        }
    }
}