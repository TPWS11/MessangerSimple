package com.tpws.massenger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tpws.massenger.R;
import com.tpws.massenger.models.ModelUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profil;
    private EditText username, et_status;
    private ImageView save;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;

    private String email;
    private String password;
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        pg = new ProgressDialog(this);
        pg.setMessage("Please wait");
        pg.setCancelable(false);


        profil = findViewById(R.id.ci_profil_setting);
        username = findViewById(R.id.et_setting_user);
        et_status = findViewById(R.id.et_setting_status);
        save = findViewById(R.id.iv_save);

        DatabaseReference reference = database.getReference().child("Users").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("Upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();

                username.setText(name);
                et_status.setText(status);
                Picasso.get().load(image).into(profil);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg.show();

                String name = username.getText().toString();
                String status = et_status.getText().toString();

                if (imageUri != null) {
                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String ImageURI = uri.toString();
                                            ModelUser user = new ModelUser(auth.getUid(), name, email, password, ImageURI, status);
                                            reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    if (task.isSuccessful()) {
                                                        pg.dismiss();
                                                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                                        finish();
                                                    } else {
                                                        pg.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImage = uri.toString();
                            ModelUser user = new ModelUser(auth.getUid(), name, email, password, finalImage, status);
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pg.dismiss();
                                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        pg.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.getData();
                profil.setImageURI(imageUri);
            }
        }
    }
}