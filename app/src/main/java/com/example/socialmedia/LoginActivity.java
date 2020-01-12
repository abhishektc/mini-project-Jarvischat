package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter=5;
    private TextView userRegistration;
    private FirebaseAuth firebaseauth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        Name=(EditText)findViewById(R.id.editText);
        Password=(EditText)findViewById(R.id.editText2);
        Info=(TextView) findViewById(R.id.tvInfo);
        Login=(Button) findViewById(R.id.button);
        userRegistration=(TextView)findViewById(R.id.tvRegister);

        Info.setText("No of attempts remaininng: 5");

        firebaseauth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        FirebaseUser user= firebaseauth.getCurrentUser();

        if(user!=null){
            finish();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));

        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validating()) {
                    validate(Name.getText().toString(), Password.getText().toString());
                }
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterUser.class));
            }
        });
    }

    private void validate(String userName, String userPassword){

        progressDialog.setMessage("Verifiying!");
        progressDialog.show();
        firebaseauth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));

                }
                else {
                    Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                    counter--;
                    Info.setText("No of attemps remaining: "+counter);
                    progressDialog.dismiss();
                    if (counter==0){
                        Login.setEnabled(false);
                    }
                }
            }
        });
    }
    private boolean validating(){
        Boolean result=false;

        String email=Name.getText().toString();
        String password=Password.getText().toString();

        if (password.isEmpty() || email.isEmpty()){
            Toast.makeText(this, "Please Enter all the details", Toast.LENGTH_SHORT).show();
        }
        else {
            result=true;
        }
        return result;
    }
}
