package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.adapters.AdapterChat;
import com.example.socialmedia.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv,userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisEmail;
    String myEmail,myUid;
    String hisImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        Toolbar toolbar=findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        toolbar.setTitle( "" );
        recyclerView=findViewById( R.id.chat_recyclerView );
        profileIv=findViewById( R.id.profileIv );
        nameTv=findViewById( R.id.nameTv );
        userStatusTv=findViewById( R.id.userStatusTv );
        messageEt=findViewById( R.id.messageEt );
        sendBtn=findViewById( R.id.sendBtn );


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager( this );
        linearLayoutManager.setStackFromEnd( true );

        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( linearLayoutManager );

        Intent intent=getIntent();
        hisEmail=intent.getStringExtra( "hisEmail" );


        firebaseAuth=FirebaseAuth.getInstance();

        firebaseDatabase=FirebaseDatabase.getInstance();
        userDbRef=firebaseDatabase.getReference("Users");

        Query userQuery=userDbRef.orderByChild( "email" ).equalTo( hisEmail );

        userQuery.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String name=""+ds.child( "name" ).getValue();
                    hisImage=""+ds.child( "image" ).getValue();

                    String typingStatus=""+ds.child( "typingTo" ).getValue();
                    if(typingStatus.equals( myEmail )) {
                        userStatusTv.setText( "typing..." );
                    }
                    else {
                        String onlineStatus=""+ds.child( "onlineStatus" ).getValue();
                        if (onlineStatus.equals( "online" )) {
                            userStatusTv.setText( onlineStatus );
                        }
                        else {
                            Calendar cal=Calendar.getInstance( Locale.ENGLISH);
                            cal.setTimeInMillis( Long.parseLong( onlineStatus ) );
                            String dateTime= DateFormat.format( "dd/MM/yyyy hh:mm aa",cal ).toString();
                            userStatusTv.setText( "Last seen at: "+ dateTime );
                        }
                    }
                    
                    nameTv.setText( name );

                    try {
                        Picasso.get().load(hisImage).into( profileIv );
                    }
                    catch (Exception e){

                        //Picasso.get().load(R.drawable.ic_add_image).into( profileIv );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        sendBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=messageEt.getText().toString().trim();

                if (TextUtils.isEmpty( message )){
                    Toast.makeText( ChatActivity.this,"Cannot send the empty message...",Toast.LENGTH_SHORT ).show();
                }
                else {
                    sendMessage(message);
                }

            }
        } );

        messageEt.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()==0) {
                    checkTypingStatus( "noOne" );
                }
                else {
                    checkTypingStatus( hisEmail );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        readMessages();

        seenMessage();
    }

    private void seenMessage() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=userRefForSeen.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals( myEmail ) && chat.getSender().equals( hisEmail )) {
                        HashMap<String, Object> hasSeenHashMap=new HashMap<>(  );
                        hasSeenHashMap.put( "isSeen",true );
                        ds.getRef().updateChildren( hasSeenHashMap );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void readMessages() {
        chatList=new ArrayList<>(  );
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals( myEmail )&& chat.getSender().equals( hisEmail ) ||
                            chat.getReceiver().equals( hisEmail ) && chat.getSender().equals( myEmail )) {
                        chatList.add( chat );
                    }

                    adapterChat=new AdapterChat( ChatActivity.this,chatList,hisImage );
                    adapterChat.notifyDataSetChanged();

                    recyclerView.setAdapter( adapterChat );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void sendMessage(String message){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        String timestamp=String.valueOf( System.currentTimeMillis() );

        HashMap<String, Object> hashMap=new HashMap<>(  );
        hashMap.put("sender",myEmail);
        hashMap.put( "receiver",hisEmail );
        hashMap.put( "message",message );
        hashMap.put( "timestamp", timestamp);
        hashMap.put( "isSeen",false );
        databaseReference.child( "Chats" ).push().setValue( hashMap );

        messageEt.setText( "" );


    }

    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){
            myEmail=user.getEmail();
            myUid=user.getUid();
        }
        else{
            startActivity( new Intent( this,LoginActivity.class ) );
            finish();
        }
    }

    private void checkOnlineStatus(String status) {

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child( myUid );
        HashMap<String, Object> hashMap=new HashMap<>(  );
        hashMap.put("onlineStatus",status);

        dbRef.updateChildren( hashMap );

    }

    private void checkTypingStatus(String typing) {

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child( myUid );
        HashMap<String, Object> hashMap=new HashMap<>(  );
        hashMap.put("typingTo",typing);

        dbRef.updateChildren( hashMap );

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus( "online" );
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp=String.valueOf( System.currentTimeMillis() );
        checkOnlineStatus( timestamp );
        checkTypingStatus( "noOne");
        userRefForSeen.removeEventListener( seenListener );
    }

    @Override
    protected void onResume() {

        checkOnlineStatus( "online" );
        super.onResume();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate( R.menu.menu,menu );
//
//        menu.findItem( R.id.action_search ).setVisible( false );
//        return super.onCreateOptionsMenu( menu );
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        int id=item.getItemId();
//        if (id==R.id.logoutMenu){
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }
//
//        return super.onOptionsItemSelected( item );
//    }
}
