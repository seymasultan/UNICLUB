package com.example.UNICLUB.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNICLUB.Cerceve.ProfilFragment;
import com.example.UNICLUB.Model.Kullanici;
import com.example.UNICLUB.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KullaniciAdapter extends RecyclerView.Adapter<KullaniciAdapter.ViewHolder>{

    private Context mContext;
    private List<Kullanici> mKullanicilar;

    private FirebaseUser firebaseKullanici;

    public KullaniciAdapter(Context mContext, List<Kullanici> mKullanicilar) {
        this.mContext = mContext;
        this.mKullanicilar = mKullanicilar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {



        View view = LayoutInflater.from(mContext).inflate(R.layout.kullanici_ogesi , parent, false);

        return new KullaniciAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {

        firebaseKullanici = FirebaseAuth.getInstance().getCurrentUser();

        final Kullanici kullanici = mKullanicilar.get(position);

        viewHolder.btn_takipEt.setVisibility(View.VISIBLE);

        viewHolder.kullaniciadi.setText(kullanici.getKullaniciadi());
        viewHolder.ad.setText(kullanici.getAd());
        Glide.with(mContext).load(kullanici.getResimurl()).into(viewHolder.profil_resmi);

        takipEdiliyor(kullanici.getId(), viewHolder.btn_takipEt);

        if(kullanici.getId().equals(firebaseKullanici.getUid()))
        {
            viewHolder.btn_takipEt.setVisibility(View.GONE);

            FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId())
                    .child("takipEdilenler").child(kullanici.getId()).setValue(true);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" , kullanici.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();

            }
        });

        viewHolder.btn_takipEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.btn_takipEt.getText().toString().equals("Takip Et"))
                {
                     FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid())
                             .child("takipEdilenler").child(kullanici.getId()).setValue(true);


                     FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId())
                             .child("takipciler").child(firebaseKullanici.getUid()).setValue(true);
                }else
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid())
                            .child("takipEdilenler").child(kullanici.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId())
                            .child("takipciler").child(firebaseKullanici.getUid()).removeValue();
                }
            }
        });
    }


    @Override
    public int getItemCount()
    {

        return mKullanicilar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView kullaniciadi;
        public TextView ad;
        public CircleImageView profil_resmi;
        public Button btn_takipEt;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            kullaniciadi = itemView.findViewById(R.id.txt_kullaniciadi_oge);
            ad = itemView.findViewById(R.id.txt_ad_oge);
            profil_resmi = itemView.findViewById(R.id.profil_resmi_oge);
            btn_takipEt = itemView.findViewById(R.id.btn_takipEt_oge);

        }
    }

    private void takipEdiliyor (final String kullaniciId , final Button button)
    {
        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(firebaseKullanici.getUid()).child("takipEdilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(kullaniciId).exists())
                {

                    button.setText("Takip Ediliyor");
                    button.setBackgroundResource(R.drawable.butonenable);
                   // button.setTextColor(R.color.butonmavisi);

                }else
                {

                    button.setText("Takip Et");
                    button.setBackgroundResource(R.drawable.buton_disable);
                    //button.setTextColor(R.color.colorAccent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
