package com.tpws.massenger.adapters;

import static com.tpws.massenger.activities.ChatActivity.rImage;
import static com.tpws.massenger.activities.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.tpws.massenger.R;
import com.tpws.massenger.models.ModelChat;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ModelChat> chats;
    int ITEM_SEND = 1;
    int ITEM_RECIV = 2;

    public ChatAdapter(Context context, ArrayList<ModelChat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layou_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item, parent, false);
            return new ReciverHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelChat chat = chats.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.tvCht.setText(chat.getMessage());

            Picasso.get().load(sImage).into(viewHolder.circleImageView);

        } else {
            ReciverHolder viewHolder = (ReciverHolder) holder;
            viewHolder.tvCht.setText(chat.getMessage());

            Picasso.get().load(rImage).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        ModelChat modelChat = chats.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(modelChat.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIV;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tvCht;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.ci_profil_user_sender);
            tvCht = itemView.findViewById(R.id.tv_sender);
        }
    }

    class ReciverHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tvCht;

        public ReciverHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.ci_profil_user_reciv);
            tvCht = itemView.findViewById(R.id.tv_reciv);
        }
    }
}