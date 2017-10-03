package com.example.laci.listazas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            //profile activity
        }

        progressDialog = new ProgressDialog(this);

        buttonSignIn = (Button) findViewById(R.id.btn_signin);
        editTextEmail = (EditText)findViewById(R.id.eT_email_login);
        editTextPassword = (EditText)findViewById(R.id.eT_password_login);
        textViewSignup = (TextView) findViewById(R.id.tv_signup);

        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    private void userLogin(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            toastMessage("Adj meg egy email címet!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            toastMessage("Adj meg egy jelszavat!");
            return;
        }

        progressDialog.setMessage("bejelentkezés...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    //start profile activity
                    toastMessage("sikeres belépés");
                }else{
                    toastMessage("sikertelen belépés");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }
        if(view == textViewSignup){
            finish();
            startActivity(new Intent(LoginActivity.this, FirebaseActivity.class));
        }
    }
}
