package com.tpws.massenger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpws.massenger.R;
import com.tpws.massenger.adapters.ChatAdapter;
import com.tpws.massenger.models.ModelChat;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverImage, ReceiverUid, ReceiverName, senderUID, senderRoom, reciverRoom;
    private CircleImageView profil;
    private TextView userName;
    public static String sImage;
    public static String rImage;
    private CardView send;
    private EditText msg;

    FirebaseDatabase database;
    FirebaseAuth auth;

    private RecyclerView rcv_msg;
    private ArrayList<ModelChat> chats;

    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ReceiverImage = getIntent().getStringExtra("image");
        ReceiverUid = getIntent().getStringExtra("uid");
        ReceiverName = getIntent().getStringExtra("name");

        chats = new ArrayList<>();

        profil = findViewById(R.id.ci_profil_user_chat);
        userName = findViewById(R.id.tv_user_name);
        send = findViewById(R.id.cd_send);
        msg = findViewById(R.id.et_message);
        rcv_msg = findViewById(R.id.rcv_massage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rcv_msg.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(ChatActivity.this, chats);
        rcv_msg.setAdapter(chatAdapter);

        Picasso.get().load(ReceiverImage).into(profil);
        userName.setText("" + ReceiverName);

        senderUID = auth.getUid();

        senderRoom = senderUID + ReceiverUid;
        reciverRoom = ReceiverUid + senderUID;




        DatabaseReference reference = database.getReference().child("Users").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("Chats").child(senderRoom).child("Messages");


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelChat modelChat = dataSnapshot.getValue(ModelChat.class);
                    chats.add(modelChat);
                }

                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msg.getText().toString();
                if (message.isEmpty()) {
                    return;
                }
                msg.setText("");
                Date date = new Date();

                ModelChat chat = new ModelChat(message, senderUID, date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .push()
                        .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("Chats")
                                        .child(reciverRoom)
                                        .child("Messages")
                                        .push()
                                        .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });

            }
        });

    }
}