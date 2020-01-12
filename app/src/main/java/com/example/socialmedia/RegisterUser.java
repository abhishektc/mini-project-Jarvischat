package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUser extends AppCompatActivity {
    private EditText userName,userEmail,userPassword,userPhone,userAge;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;
    String name,email,password,phone,uid;
    private DatabaseReference ProductRef;
    private ProgressDialog progressDialog;
    private String user_email,user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_user );

        setUIViews();

        ProductRef= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()) {
                    //upload data to the database
                    user_email=userEmail.getText().toString().trim();
                    user_password=userPassword.getText().toString().trim();

                    progressDialog.setMessage("Verifiying!");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                uid =firebaseAuth.getCurrentUser().getUid();
                                SenduserData();
                                Toast.makeText(RegisterUser.this,"Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterUser.this,LoginActivity.class));
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterUser.this,"Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUser.this,LoginActivity.class));

            }
        });
    }

    private void setUIViews(){
        userName=(EditText)findViewById(R.id.userName);
        userPassword=(EditText)findViewById(R.id.etUserPass);
        userEmail=(EditText)findViewById(R.id.etUserEmail);
        regButton=(Button)findViewById(R.id.btn);
        userLogin=(TextView)findViewById(R.id.already);
        userPhone=(EditText) findViewById(R.id.userPhone);
        userProfilePic=(ImageView)findViewById(R.id.imageView);
    }
    private boolean validate(){
        Boolean result=false;

        name=userName.getText().toString();
        password=userPassword.getText().toString();
        email=userEmail.getText().toString();
        //eemail=userEmail.getText().toString();
        phone=userPhone.getText().toString();


        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()){
            Toast.makeText(this, "Please Enter all the details", Toast.LENGTH_SHORT).show();
        }
        else if (password.length()<6){
            Toast.makeText(this, "Password Minimum Length is 6", Toast.LENGTH_SHORT).show();

        }
        else {
            result=true;
            //email= email.replace(".",",");
        }
        return result;
    }
    private void SenduserData(){
//        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
//        DatabaseReference myRef=firebaseDatabase.getReference(firebaseAuth.getUid());
//        UserProfile userProfile=new UserProfile(name,email,phone);
//        myRef.setValue(userProfile);

        HashMap<String,Object> productMap=new HashMap<>();

        productMap.put("name",name);
        productMap.put("email",email);
        productMap.put("phone",phone);
        productMap.put( "image","" );
        productMap.put("onlineStatus","online");
        productMap.put("typingTo","noOne");
        productMap.put("password",password);
        productMap.put("uid",uid);

        ProductRef.child(uid).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {

                }
                else
                {

                    String message=task.getException().toString();
                    Toast.makeText(RegisterUser.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
