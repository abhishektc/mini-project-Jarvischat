package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseauth;

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ActionBar actionBar=getSupportActionBar();
        //actionBar.setTitle("Profile");

        firebaseauth=FirebaseAuth.getInstance();

        BottomNavigationView navigationView=findViewById( R.id.navigation );
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //actionBar.setTitle( "Home" );
        HomeFragment fragment1=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace( R.id.content, fragment1,"" );
        ft1.commit();

        FirebaseUser user=firebaseauth.getCurrentUser();
        if (user!=null){

        }
        else{
            startActivity( new Intent( MainActivity.this,LoginActivity.class ) );
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                     //actionBar.setTitle( "Home" );
                     HomeFragment fragment1=new HomeFragment();
                     FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                     ft1.replace( R.id.content, fragment1,"" );
                     ft1.commit();
                     return true;
                case R.id.nav_profile:
                     //actionBar.setTitle( "Profile" );
                     PofileFragment fragment2=new PofileFragment();
                     FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                     ft2.replace( R.id.content, fragment2,"" );
                     ft2.commit();
                     return true;
                case R.id.nav_users:
                     //actionBar.setTitle( "Users" );
                     UsersFragment fragment3=new UsersFragment();
                     FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                     ft3.replace( R.id.content, fragment3,"" );
                     ft3.commit();
                     return true;
            }
            return false;
        }
    };

    private void Logout(){
        firebaseauth.signOut();
        finish();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }


}
