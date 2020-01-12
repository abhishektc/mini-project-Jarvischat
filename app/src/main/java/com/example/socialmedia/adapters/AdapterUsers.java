package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.ChatActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.models.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUsers> usersList;

    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate( R.layout.row_users, viewGroup,false);

        return new MyHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        String userImage=usersList.get( i ).getImage();
        String userName=usersList.get( i ).getName();
        final String userEmail=usersList.get( i ).getEmail();

        myHolder.mNameTv.setText( userName );
        myHolder.mEmailTv.setText( userEmail );
        try {
            Picasso.get().load(userImage)
                    .placeholder( R.drawable.ic_default_img )
                    .into( myHolder.mAvatarIv );
        }
        catch (Exception e){

        }

        myHolder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText( context,""+userEmail,Toast.LENGTH_SHORT ).show();
                Intent intent=new Intent( context, ChatActivity.class );
                intent.putExtra( "hisEmail", userEmail);
                context.startActivity( intent );
            }
        } );

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;

        public MyHolder(@NonNull View itemView) {
            super( itemView );

            mAvatarIv=itemView.findViewById( R.id.avatarIv );
            mNameTv=itemView.findViewById( R.id.nameTv );
            mEmailTv=itemView.findViewById( R.id.emailTv );

        }
    }
}
