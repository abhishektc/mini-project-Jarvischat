package com.example.socialmedia;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PofileFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceNew;

    ImageView avatarIv;
    private Uri ImageUri;
    private StorageReference ProductImagesRef;
    TextView nameTv, emailTv, phoneTv;
    private String productRandomKey,downloadImageUrl,title,date,description;
    private static final int GalleryPick = 1;
    Button uploadBtn;

    private ProgressDialog progressDialog;

    public PofileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate( R.layout.fragment_pofile, container, false );


        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Users Image");

        FirebaseUser user=firebaseAuth.getCurrentUser();
        String myUid=user.getUid();

        databaseReferenceNew=firebaseDatabase.getReference("Users").child(myUid);

        uploadBtn=view.findViewById( R.id.upload_btn );

        avatarIv=view.findViewById( R.id.avatarIv );
        nameTv=view.findViewById( R.id.nameTv );
        emailTv=view.findViewById( R.id.emailTv );
        phoneTv=view.findViewById( R.id.phoneTv );

        progressDialog=new ProgressDialog(getActivity());

        avatarIv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        } );

        uploadBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading!");
                progressDialog.show();
                uploadImage();

            }
        } );

        Query query=databaseReference.orderByChild( "email" ).equalTo( user.getEmail() );
        query.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name=""+ds.child( "name" ).getValue();
                    String email=""+ds.child( "email" ).getValue();
                    String phone=""+ds.child( "phone" ).getValue();
                    String image=""+ds.child( "image" ).getValue();

                    nameTv.setText( name );
                    emailTv.setText( email );
                    phoneTv.setText( phone );
                    try {
                        Picasso.get().load(image).into( avatarIv );
                    }
                    catch (Exception e){

                        Picasso.get().load(R.drawable.ic_add_image).into( avatarIv );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        return view;
    }

    private void uploadImage()
    {

        final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment()+ productRandomKey+".jpg");
        final UploadTask uploadTask=filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText(getActivity(), "Error: "+message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {

                Task<Uri> urlTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl=task.getResult().toString();

                            HashMap<String,Object> productMap=new HashMap<>();

                            productMap.put( "image",downloadImageUrl );

                            databaseReferenceNew.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String message=task.getException().toString();
                                        Toast.makeText(getActivity(), "Error: "+message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                });
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){

        }
        else{
            startActivity( new Intent( getActivity(),LoginActivity.class ) );
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu( true );
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate( R.menu.menu,menu );
        menu.findItem( R.id.action_search ).setVisible( false );
        super.onCreateOptionsMenu( menu, inflater );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.logoutMenu){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected( item );
    }

    private void openGallery() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            avatarIv.setImageURI(ImageUri);
            uploadBtn.setVisibility( View.VISIBLE );
        }


    }
}
