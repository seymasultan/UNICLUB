package com.example.UNICLUB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class KaydolActivity extends AppCompatActivity {

    EditText edt_kullaniciAdi, edt_Ad, edt_Email, edt_Sifre;
    Button btn_Kaydol;

    TextView txt_GirisSayfasinaGit;

    FirebaseAuth yetki;
    DatabaseReference yol;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);

        edt_kullaniciAdi = findViewById(R.id.edt_kullaniciAdi);
        edt_Ad = findViewById(R.id.edt_Ad);
        edt_Email = findViewById(R.id.edt_Email);
        edt_Sifre = findViewById(R.id.edt_Sifre);

        btn_Kaydol = findViewById(R.id.btn_Kaydol_activity);

        txt_GirisSayfasinaGit = findViewById(R.id.txt_girisSayfasina_git);

        yetki = FirebaseAuth.getInstance();

        txt_GirisSayfasinaGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KaydolActivity.this,GirisActivity.class));
            }
        });

        btn_Kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(KaydolActivity.this);
                pd.setMessage("Lütfen bekleyin");
                pd.show();

                String str_kullaniciAdi = edt_kullaniciAdi.getText().toString();
                String str_Ad = edt_Ad.getText().toString();
                String str_Email = edt_Email.getText().toString();
                String str_Sifre = edt_Sifre.getText().toString();

                if(TextUtils.isEmpty(str_kullaniciAdi)|| TextUtils.isEmpty(str_Ad)|| TextUtils.isEmpty(str_Email) || TextUtils.isEmpty(str_Sifre)){
                    Toast.makeText(KaydolActivity.this, "Lütfen bütün alanları doldurun", Toast.LENGTH_SHORT).show();
                }else if (str_Sifre.length()<6)
                {
                    Toast.makeText(KaydolActivity.this, "Şifreniz en az 6 karakter olmalı", Toast.LENGTH_SHORT).show();
                }else
                {
                    kaydet(str_kullaniciAdi, str_Ad, str_Email,str_Sifre);
                }

            }
        });




    }

    private void kaydet(final String kullaniciadi, final String ad, String email , String sifre)
    {
        yetki.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener(KaydolActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

               final FirebaseUser firebaseKullanici = yetki.getCurrentUser();

                String kullaniciId = firebaseKullanici.getUid();

                yol = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(kullaniciId);

                //birden fazla veriyi aynı anda göndermek için hashmap
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", kullaniciId);
                hashMap.put("kullaniciadi", kullaniciadi.toLowerCase());
                hashMap.put("ad", ad);
                hashMap.put("bio", "");
                hashMap.put("resimurl", "https://firebasestorage.googleapis.com/v0/b/instagram-dcf9b.appspot.com/o/placeholder.jpg?alt=media&token=c6dbf683-3c03-44ea-9e26-294a19da4567");

                yol.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            pd.dismiss();
                            FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid())
                                    .child("takipEdilenler").child(firebaseKullanici.getUid()).setValue(true);
                            Intent intent = new Intent (KaydolActivity.this, AnaActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


                        }

                    }
                });

                }else{

                    pd.dismiss();
                    Toast.makeText(KaydolActivity.this, "Bu mail ve şifre ile kayıt başarısız", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
