package com.example.UNICLUB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.UNICLUB.Model.Kullanici;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class ProfilActivity extends AppCompatActivity {

    ImageView profilresmi;
    EditText  kullaniciAdi;
    EditText  adisoyadi;
    EditText  hakkında;
    Button kaydet;

    FirebaseUser mevcutKullanici;
    Uri resimUri;
    String benimUrim = "";

    StorageTask yuklemeGorevi;
    StorageReference resimYukeYolu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Lütfen Bekleyin..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        final Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1090);


        profilresmi=findViewById(R.id.duzenle_profilresmi);
        kullaniciAdi=findViewById(R.id.duzenle_kullaniciAdi);
        adisoyadi=findViewById(R.id.duzenle_adisoyadi);
        hakkında=findViewById(R.id.duzenle_hakkında);
        kaydet=findViewById(R.id.kaydet);



        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        resimYukeYolu = FirebaseStorage.getInstance().getReference("profiller");

        kullaniciBilgisi();

        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String kullaniciadi=kullaniciAdi.getText().toString();
                String ad=adisoyadi.getText().toString();
                String bio=hakkında.getText().toString();

                save(kullaniciadi,ad,bio);

            }
        });

        profilresmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(ProfilActivity.this);
            }
        });

    }

    private void kullaniciBilgisi(){

        DatabaseReference kullaniciYolu= FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);

                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(profilresmi);
                kullaniciAdi.setText(kullanici.getKullaniciadi());
                adisoyadi.setText(kullanici.getAd());
                hakkında.setText(kullanici.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void save(String kullaniciadi,String ad,String bio){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Kullanicilar").child(mevcutKullanici.getUid()).child("ad").setValue(ad);
        databaseReference.child("Kullanicilar").child(mevcutKullanici.getUid()).child("bio").setValue(bio);
        databaseReference.child("Kullanicilar").child(mevcutKullanici.getUid()).child("kullaniciadi").setValue(kullaniciadi);

        resimYukle();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK )
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri = result.getUri();

            profilresmi.setImageURI(resimUri);
        }else
        {
            Toast.makeText(this, "Resim Seçilemedi", Toast.LENGTH_SHORT).show();
            finish();

        }
    }
    private String dosyaUzantisiAl ( Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void resimYukle() {


        if(resimUri != null)
        {


            final StorageReference dosyaYolu = resimYukeYolu.child(System.currentTimeMillis()
                    +"."+dosyaUzantisiAl(resimUri));

            yuklemeGorevi = dosyaYolu.putFile(resimUri);
            yuklemeGorevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return dosyaYolu.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful())
                    {
                        Uri indirmeUrisi = task.getResult();
                        benimUrim = indirmeUrisi.toString();
                        System.out.println(benimUrim);

                      /*  DatabaseReference veriyolu = FirebaseDatabase.getInstance().getReference("profiller");

                        String  profilId = veriyolu.push().getKey();
                        //çoklu gönderiyi veritabaınıa göndermek için listeye benxeyen yapılar
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("profilId",profilId);
                        hashMap.put("resimurl", benimUrim);
                        hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        veriyolu.child(profilId).setValue(hashMap); */
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

                        databaseReference.child("Kullanicilar").child(mevcutKullanici.getUid()).child("resimurl").setValue(benimUrim);
                        finish();

                        Toast.makeText(ProfilActivity.this, "Güncelleme Başarılı", Toast.LENGTH_SHORT).show();

                    }else
                    {
                        Toast.makeText(ProfilActivity.this, "Yükleme Başarısız !", Toast.LENGTH_SHORT).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Lütfen Bekleyin..");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(5);
            progressDialog.show();


            Toast.makeText(this, "Güncelleme Başarılı", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Resim değiştirilmedi.", Toast.LENGTH_SHORT).show();

        }






    }
}
