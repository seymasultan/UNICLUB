package com.example.UNICLUB.Cerceve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.UNICLUB.Model.Gonderi;
import com.example.UNICLUB.Model.Kullanici;
import com.example.UNICLUB.OptionsActivity;
import com.example.UNICLUB.ProfilActivity;
import com.example.UNICLUB.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {

    List<Gonderi> gonderiListeleri;
    ImageView resimSecenekler,profil_resmi;
    TextView txt_gonderiler,txt_takipciler,txt_takipEdilenler,txt_ad,txt_bio,txt_kullaniciAdi;
    Button btn_profili_duzenle, btn_kulupKatıl;


    FirebaseUser mevcutKullanici;
    String profilId;
    androidx.gridlayout.widget.GridLayout grid_layout;
    public Activity mActivity;

    public ProfilFragment() {
        // Required empty public constructor


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profil, container, false);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        profilId=prefs.getString("profileid","none");

        resimSecenekler=view.findViewById(R.id.resimSecenekler_profilCercevesi);
        profil_resmi=view.findViewById(R.id.profil_resmi_profilCercevesi);

        txt_gonderiler=view.findViewById(R.id.txt_gonderiler_profilCercevesi);
        txt_takipciler=view.findViewById(R.id.txt_takipciler_profilCercevesi);
        txt_takipEdilenler=view.findViewById(R.id.txt_takipEdilenler_profilCercevesi);
        txt_ad=view.findViewById(R.id.txt_ad_profilCercevesi);
        txt_bio=view.findViewById(R.id.txt_bio_profilCercevesi);
        txt_kullaniciAdi=view.findViewById(R.id.txt_kullaniciadi_profilCerceve);

        btn_profili_duzenle=view.findViewById(R.id.btn_profiliDuzenle_profilCercevesi);
        btn_kulupKatıl =view.findViewById(R.id.btn_kulupAc_profilCercevesi);

        grid_layout=view.findViewById(R.id.grid);
        grid_layout.setColumnCount(2);
        grid_layout.setRowCount(2);
        mActivity=getActivity();

        //metotları çağır
        kullaniciBilgisi();
        takipcileriAl();
        gonderiSayısıAl();
        gonderileriOku();

        if(profilId.equals(mevcutKullanici.getUid())){

            btn_profili_duzenle.setText("Profili Düzenle");

        }
        else{
            takipKontrolu();
        }

        btn_profili_duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn=btn_profili_duzenle.getText().toString();

                if(btn.equals("Profili Düzenle")){

                    Intent intent=new Intent(getActivity(),ProfilActivity.class);
                    startActivity(intent);
                }
                else if(btn.equals("Takip Et")){

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profilId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipciler").child(mevcutKullanici.getUid()).setValue(true);
                }
                else if(btn.equals("Takip Ediliyor")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profilId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipciler").child(mevcutKullanici.getUid()).removeValue();
                }
            }
        });

        resimSecenekler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        btn_kulupKatıl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_kulupKatıl.getText().toString().equals("Üyeleri Gör")) {
                    Toast.makeText(mActivity, "Üyeniz yok", Toast.LENGTH_SHORT).show();
                    Log.i("LOGDENEME", "Üye Yoook");
                } else if (btn_kulupKatıl.getText().toString().equals("Bu Kulübe katıl")) {

                    dialogAc();
                }
            }
        });

        kulupMu();
        return view;
    }


    private void kullaniciBilgisi(){

        DatabaseReference kullaniciYolu=FirebaseDatabase.getInstance().getReference("Kullanicilar").child(profilId);
        System.out.println("-------------"+profilId);
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(getContext()==null){
                    return;
                }

                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);

                Glide.with(getContext()).load(kullanici.getResimurl()).into(profil_resmi);
                txt_kullaniciAdi.setText(kullanici.getKullaniciadi());
                txt_ad.setText(kullanici.getAd());
                txt_bio.setText(kullanici.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void takipKontrolu(){

        DatabaseReference takipYolu=FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                .child("takipEdilenler");

        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(profilId).exists()){ //eğer profilId varsa takip ediyordur
                    btn_profili_duzenle.setText("Takip Ediliyor");
                }else{
                    btn_profili_duzenle.setText("Takip Et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takipcileriAl(){

        //takipci sayısı al
        DatabaseReference takipciYolu=FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId).child("takipciler");

        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                txt_takipciler.setText(" "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Takip edilen sayısı al
        DatabaseReference takipEdilenYolu=FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId).child("takipEdilenler");

        takipEdilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                txt_takipEdilenler.setText(" "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gonderiSayısıAl(){

        DatabaseReference gonderiYolu=FirebaseDatabase.getInstance().getReference().child("Gonderiler");

        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){  //bunun çocuklarını al

                    Gonderi gonderi=snapshot.getValue(Gonderi.class);

                    if(gonderi.getGonderen().equals(profilId)){
                        i++;
                    }
                }

                txt_gonderiler.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void gonderileriOku()
    {

        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");

        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                gonderiListeleri= new ArrayList<Gonderi>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);

                        if(gonderi.getGonderen().equals(profilId))
                        {
                            ImageView imageView=new ImageView(getActivity().getApplicationContext());
                            Glide.with(mActivity).load(gonderi.getGonderiResmi()).into(imageView);
                            grid_layout.addView(imageView,540,540);

                        }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void dialogAc() {

        //bu activity için inflate oluşturduk eğer adapter de olsaydı activity.getLayotInflater dememiz gerekirdi.
        LayoutInflater ınflater = getLayoutInflater();

        // oluşturduğumuz layout u kullanmak için view a tanımlıyoruz
        View view = ınflater.inflate(R.layout.kulupkatil_alert, null);

        final EditText etxt_ad = view.findViewById(R.id.alert_form_adi);
        final EditText etxt_soyadi = view.findViewById(R.id.alert_form_soyadi);
        final EditText etxt_tel = view.findViewById(R.id.alert_form_tel);
        final EditText etxt_email = view.findViewById(R.id.alert_form_email);
        Button btnKatil = view.findViewById(R.id.alert_form_kaydolbtn);

        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(view); // görüntülenecek olan view yukarıda tanımladığımız.
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();

        btnKatil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmail(etxt_email.getText().toString())) {

                    Toast.makeText(mActivity, "kayıt Başarılı\n"
                            + etxt_ad.getText().toString() + "\n"
                            + etxt_soyadi.getText().toString() + "\n"
                            + etxt_tel.getText().toString() + "\n"
                            + etxt_email.getText().toString(), Toast.LENGTH_SHORT).show();

                    dialog.cancel();
  /**/                btn_kulupKatıl.setText("Kulübe Katıldınız");

                }else{
                    Toast.makeText(mActivity, "Geçerli bir email değil!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private void kulupMu(){

        DatabaseReference veriyolu = FirebaseDatabase.getInstance().getReference("Kulüpler");

        veriyolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(profilId.equals(mevcutKullanici.getUid()) && dataSnapshot.child(profilId).exists()){


                    btn_kulupKatıl.setText("Üyeleri Gör");

                }else if(dataSnapshot.child(profilId).exists())
                {
                    btn_kulupKatıl.setText("Bu Kulübe katıl");
                }
                else{

                     btn_kulupKatıl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
