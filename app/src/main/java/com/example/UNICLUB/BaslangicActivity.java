package com.example.UNICLUB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaslangicActivity extends AppCompatActivity {

    Button btn_baslangicGiris;
    Button btn_baslangicKaydol;

    FirebaseUser baslangicKullanici;

    @Override
    protected void onStart() {
        super.onStart();

        baslangicKullanici = FirebaseAuth.getInstance().getCurrentUser();

        if(baslangicKullanici != null)
        {
            startActivity(new Intent( BaslangicActivity.this, AnaActivity.class));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baslangic);

        btn_baslangicGiris = findViewById(R.id.btn_giris);
        btn_baslangicKaydol = findViewById(R.id.btn_kayit);

        btn_baslangicGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(BaslangicActivity.this, GirisActivity.class));
            }
        });

        btn_baslangicKaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(BaslangicActivity.this, KaydolActivity.class));
            }
        });
    }
}
