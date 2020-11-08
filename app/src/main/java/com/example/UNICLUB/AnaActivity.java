package com.example.UNICLUB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.example.UNICLUB.Cerceve.AramaFragment;
import com.example.UNICLUB.Cerceve.HomeFragment;
import com.example.UNICLUB.Cerceve.ProfilFragment;
import com.example.UNICLUB.Model.Kullanici;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AnaActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Fragment seciliCerceve = null;
    FirebaseUser mevcutKullanici;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3500);

        kulupMu();


        Bundle intent=getIntent().getExtras();

        if(intent!=null){

            String gonderen=intent.getString("gonderenId");

            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",gonderen);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, new ProfilFragment()).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, new HomeFragment()).commit();
        }




    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId())
                    {

                        case R.id.nav_home:

                            seciliCerceve = new HomeFragment();

                            break;

                        case R.id.nav_arama:

                            seciliCerceve = new AramaFragment();
                            break;

                        case R.id.nav_ekle:

                                seciliCerceve = null;
                                startActivity(new Intent(AnaActivity.this, GonderiActivity.class));
                            break;

                        case R.id.nav_profil:

                            SharedPreferences.Editor editor = getSharedPreferences("PREF", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            seciliCerceve = new ProfilFragment();
                            break;
                    }

                    if(seciliCerceve != null)
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, seciliCerceve).commit();

                    }

                    return true;
                }
            };

    private void kulupMu(){

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference veriyolu = FirebaseDatabase.getInstance().getReference("Kulüpler");

        veriyolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(mevcutKullanici.getUid()).exists()){

                    bottomNavigationView = findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    bottomNavigationView.getMenu()
                            .findItem(R.id.nav_ekle)
                            .setVisible(true);


                }else{
                    bottomNavigationView = findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
                    bottomNavigationView.getMenu()
                            .findItem(R.id.nav_ekle)
                            .setVisible(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
