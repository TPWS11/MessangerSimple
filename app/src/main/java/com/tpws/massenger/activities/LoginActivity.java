package com.tpws.massenger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tpws.massenger.R;

public class LoginActivity extends AppCompatActivity {

    private TextView sign_up;
    private EditText et_email, et_password;
    private TextView sign_btn;
    private FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        pg = new ProgressDialog(this);
        pg.setMessage("Please wait");
        pg.setCancelable(false);

        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        sign_btn = findViewById(R.id.btn_login);
        sign_up = findViewById(R.id.tv_sign_up);

        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    et_email.setError("Enter your email");
                    et_email.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    et_password.setError("Enter your password");
                    et_password.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    et_email.setError("Invalid email");
                    et_email.requestFocus();
                } else if (password.length() < 8) {
                    et_password.setError("Password to week");
                    et_password.requestFocus();
                } else {
                    pg.show();
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pg.dismiss();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                pg.dismiss();
                                Toast.makeText(LoginActivity.this, "Error in login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}