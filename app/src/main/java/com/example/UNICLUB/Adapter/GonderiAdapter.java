package com.example.UNICLUB.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNICLUB.Cerceve.ProfilFragment;
import com.example.UNICLUB.Model.Gonderi;
import com.example.UNICLUB.Model.Kullanici;
import com.example.UNICLUB.R;
import com.example.UNICLUB.YorumlarActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GonderiAdapter extends RecyclerView.Adapter<GonderiAdapter.ViewHolder> {

    public Context mContext;
    public List<Gonderi> mGonderi;
    private FirebaseUser mevcutFirebaseUser;

    public GonderiAdapter(Context mContext, List<Gonderi> mGonderi) {
        this.mContext = mContext;
        this.mGonderi = mGonderi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.gonderi_ogesi, parent ,false );


        return new GonderiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        mevcutFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Gonderi gonderi = mGonderi.get(position);


        Glide.with(mContext).load(gonderi.getGonderiResmi()).into(viewHolder.gonderi_resmi);

        if(gonderi.getGonderiHakkinda().equals(""))
        {
            viewHolder.txt_gonderiHakkinda.setVisibility(View.GONE);
        }else
        {
            viewHolder.txt_gonderiHakkinda.setVisibility(View.VISIBLE);
            viewHolder.txt_gonderiHakkinda.setText(gonderi.getGonderiHakkinda());
        }

        //metotları çağır
        gonderenBilgileri(viewHolder.profil_resmi, viewHolder.txt_kullanici_adi, viewHolder.txt_gonderen, gonderi.getGonderen());
        begenildi(gonderi.getGonderiId(),viewHolder.begeni_resmi);
        begeniSayisi(viewHolder.txt_begeni,gonderi.getGonderiId());
        yorumlariAl(gonderi.getGonderiId(),viewHolder.txt_yorumlar, viewHolder.txt_yorumsayisi);
       // timeset(viewHolder.txt_time, gonderi.getGonderiTime());

        viewHolder.begeni_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.begeni_resmi.getTag().equals("beğen")){

                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).removeValue();
                }
            }
        });

        viewHolder.txt_kullanici_adi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" ,gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();

            }
        });

        viewHolder.yorum_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, YorumlarActivity.class);
                intent.putExtra("gonderiId",gonderi.getGonderiId());
                intent.putExtra("gonderenId",gonderi.getGonderen());
                mContext.startActivity(intent);
            }
        });

        viewHolder.txt_yorumlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, YorumlarActivity.class);
                intent.putExtra("gonderiId",gonderi.getGonderiId());
                intent.putExtra("gonderenId",gonderi.getGonderen());
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount()
    {

        return mGonderi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //View tanımlama sınıfı

        public ImageView profil_resmi, gonderi_resmi, begeni_resmi, yorum_resmi , kaydetme_resmi;

        public TextView txt_kullanici_adi, txt_begeni, txt_gonderen, txt_gonderiHakkinda, txt_yorumlar, txt_yorumsayisi, txt_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profil_resmi = itemView.findViewById(R.id.profil_resmi_Gonderi_Ogesi);
            gonderi_resmi = itemView.findViewById(R.id.gonderi_resmi_Gonderi_Ogesi);
            begeni_resmi = itemView.findViewById(R.id.begeni_Gonderi_Ogesi);
            yorum_resmi = itemView.findViewById(R.id.yorum_Gonderi_Ogesi);


            txt_kullanici_adi = itemView.findViewById(R.id.txt_kullaniciadi_Gonderi_Ogesi);
            txt_begeni = itemView.findViewById(R.id.txt_begeniler_Gonderi_Ogesi);
            txt_gonderen = itemView.findViewById(R.id.txt_gonderen_Gonderi_Ogesi);
            txt_gonderiHakkinda = itemView.findViewById(R.id.txt_gonderiHakkinda_Gonderi_Ogesi);
            txt_yorumlar = itemView.findViewById(R.id.txt_yorum_Gonderi_Ogesi);
            txt_yorumsayisi = itemView.findViewById(R.id.txt_yorumSayisi_Gonderi_Ogesi);
            txt_time = itemView.findViewById(R.id.txt_time_Gonderi_Ogesi);
        }
    }

    private void yorumlariAl(String gonderiId, final TextView yorumlar, final TextView yorumsayisi){

        DatabaseReference yorumlariAlmaYolu=FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        yorumlariAlmaYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                yorumlar.setText(dataSnapshot.getChildrenCount()+" yorumun hepsini gör...");
                yorumsayisi.setText(dataSnapshot.getChildrenCount()+" yorum");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void begenildi(String gonderiId, final ImageView ımageView){

           final FirebaseUser mevcutKullanici=FirebaseAuth.getInstance().getCurrentUser();

           DatabaseReference begeniVeritabaniYolu=FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderiId);

           begeniVeritabaniYolu.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   if(dataSnapshot.child(mevcutKullanici.getUid()).exists()){

                       ımageView.setImageResource(R.drawable.ic_ok_mavi);
                       ımageView.setTag("beğenildi");

                   }
                   else{
                       ımageView.setImageResource(R.drawable.ic_ok);
                       ımageView.setTag("beğen");
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
    }

    private void begeniSayisi(final TextView begeniler, String gonderiId ){

        DatabaseReference begeniSayisiVeritabaniYolu=FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderiId);

        begeniSayisiVeritabaniYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                begeniler.setText(dataSnapshot.getChildrenCount()+" beğeni");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "Hata", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private  void gonderenBilgileri(final ImageView profil_resmi, final TextView kullanici_adi, final TextView gonderen, String kullaniciId)
    {
        DatabaseReference veriyolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(kullaniciId);

        veriyolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);

                Glide.with(mContext).load(kullanici.getResimurl()).into(profil_resmi);
                kullanici_adi.setText(kullanici.getKullaniciadi());
                gonderen.setText(kullanici.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void timeset(final TextView time, String gettime){


        if(gettime == null)
            time.setText("Uzun Zaman Önce");
        else
            time.setText(gettime);

        Log.i("ETİKET", "mesaj "+gettime );


    }
}
