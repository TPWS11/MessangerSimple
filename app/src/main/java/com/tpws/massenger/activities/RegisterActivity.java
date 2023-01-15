package com.tpws.massenger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tpws.massenger.R;
import com.tpws.massenger.models.ModelUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private TextView tv_sign_in, sign_up;
    private CircleImageView ci_profil;
    private EditText et_username, et_email, et_password, et_confirm;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    String image;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    ProgressDialog pg;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        pg = new ProgressDialog(this);
        pg.setMessage("Please wait");
        pg.setCancelable(false);

        tv_sign_in = findViewById(R.id.tv_sign_in);
        ci_profil = findViewById(R.id.ci_profil_register);
        et_username = findViewById(R.id.et_username_register);
        et_email = findViewById(R.id.et_email_register);
        et_password = findViewById(R.id.et_password_register);
        et_confirm = findViewById(R.id.et_confirm_register);
        sign_up = findViewById(R.id.btn_register);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString(),
                        email = et_email.getText().toString(),
                        password = et_password.getText().toString(),
                        cPassword = et_confirm.getText().toString(),
                        status = "Ada";

                if (TextUtils.isEmpty(username)) {
                    et_username.setError("Enter your name");
                    et_username.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    et_email.setError("Enter your email");
                    et_email.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    et_password.setError("Enter your password");
                    et_password.requestFocus();
                } else if (TextUtils.isEmpty(cPassword)) {
                    et_confirm.setError("Enter your password");
                    et_confirm.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    et_email.setError("Enter valid email");
                    et_email.requestFocus();
                } else if (password.length() < 8) {
                    et_password.setError("Password to week");
                    et_password.requestFocus();
                } else if (!cPassword.equals(password)) {
                    et_confirm.setError("Password does not match");
                    et_confirm.requestFocus();
                } else {
                    pg.show();
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference reference = database.getReference().child("Users").child(auth.getUid());
                                StorageReference storageReference = storage.getReference().child("Upload").child(auth.getUid());

                                if (imageUri != null) {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        image = uri.toString();
                                                        ModelUser user = new ModelUser(auth.getUid(), username, email, password, image, status);
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    pg.dismiss();
                                                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                                } else {
                                                                    Toast.makeText(RegisterActivity.this, "Creating user is failed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    String status = "Ada";
                                    image = "https://firebasestorage.googleapis.com/v0/b/massenger-631e8.appspot.com/o/icon_profil.jpg?alt=media&token=f6660920-1844-43a9-b444-eca115f94262";
                                    ModelUser user = new ModelUser(auth.getUid(), username, email, password, image, status);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Creating user is failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                pg.dismiss();
                                Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        ci_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });


        tv_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.getData();
                ci_profil.setImageURI(imageUri);
            }
        }
    }
}