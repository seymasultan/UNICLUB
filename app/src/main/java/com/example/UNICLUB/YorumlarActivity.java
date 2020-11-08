package com.example.UNICLUB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.UNICLUB.Adapter.YorumAdapter;
import com.example.UNICLUB.Model.Kullanici;
import com.example.UNICLUB.Model.Yorum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YorumlarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private YorumAdapter yorumAdapter;
    private List<Yorum>  yorumListesi;
    EditText edt_yorum_ekle;
    ImageView profil_resmi;
    TextView txt_gonder;

    String gonderiId;
    String gonderenId;

    FirebaseUser mevcutKullanici;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorumlar);

        final Toolbar toolbar=findViewById(R.id.toolbar_yorumlarActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yorumlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_view_yorumlarActivity);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        yorumListesi=new ArrayList<>();
        yorumAdapter=new YorumAdapter(this,yorumListesi);
        recyclerView.setAdapter(yorumAdapter);


        edt_yorum_ekle=findViewById(R.id.edit_yorumEkle_yorumlarActivity);
        profil_resmi=findViewById(R.id.profil_resmi_yorumlarActivity);
        txt_gonder=findViewById(R.id.txt_gonder_yorumlarActivity);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();

        Intent ıntent=getIntent();

        gonderiId=ıntent.getStringExtra("gonderiId");
        gonderenId=ıntent.getStringExtra("gonderenId");

        txt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_yorum_ekle.getText().toString().equals("")){
                    Toast.makeText(YorumlarActivity.this,"Boş Yorum Gönderemezsiniz",Toast.LENGTH_LONG).show();
                }
                else{
                    yorumEkle();
                }
            }
        });

        resimAl();

        yorumlarıOku();
    }

    private void yorumEkle() {

        DatabaseReference yorumlarYolu= FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);

        //Yorumlar diye alan açtı veritabanında,onun içinde de gonderiId var

        HashMap<String,Object> hashMap=new HashMap<>();  //Çoklu veri yollamak için
        hashMap.put("yorum",edt_yorum_ekle.getText().toString().trim());
        hashMap.put("gonderen",mevcutKullanici.getUid());

        yorumlarYolu.push().setValue(hashMap); //push verilerin üst üste binmemesi için
        edt_yorum_ekle.setText("");  //yorum alanını temizledi

    }

    private void resimAl(){

        DatabaseReference resimYolu=FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());
        resimYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);

                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(profil_resmi);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void yorumlarıOku(){

        DatabaseReference yorumlarıOkumaYolu=FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        yorumlarıOkumaYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                yorumListesi.clear();  //yorumlar üst üste gelmesin
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){ //veritabanındaki bütün çocukları al

                    Yorum yorum=snapshot.getValue(Yorum.class);
                    yorumListesi.add(yorum);
                }

                yorumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
