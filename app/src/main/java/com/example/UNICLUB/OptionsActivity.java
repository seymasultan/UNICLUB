package com.example.UNICLUB;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class OptionsActivity extends AppCompatActivity {

    TextView logout, kulupAc;
    BottomNavigationView bottomNavigationView;
    FirebaseUser mevcutKullanici;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        logout = findViewById(R.id.logout);
        kulupAc= findViewById(R.id.settings);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ayarlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this, BaslangicActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        kulupAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();

               databaseReference = FirebaseDatabase.getInstance().getReference();
               databaseReference.child("Kulüpler").child(mevcutKullanici.getUid()).setValue(true);

               Intent intent = new Intent(getApplicationContext(), AnaActivity.class);
               startActivity(intent);
                //       bottomNavigationView.setVisibility(View.VISIBLE);                  //HATA VERİYOR
            }
        });

    }
}
