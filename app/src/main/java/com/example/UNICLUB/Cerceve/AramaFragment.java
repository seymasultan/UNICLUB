package com.example.UNICLUB.Cerceve;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.UNICLUB.Adapter.KullaniciAdapter;
import com.example.UNICLUB.Model.Kullanici;
import com.example.UNICLUB.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AramaFragment extends Fragment {


    private RecyclerView recyclerView;
    private KullaniciAdapter kullaniciAdapter;
    private List<Kullanici> mKullaniciler;

    EditText arama_bar;

    public AramaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arama, container, false);

        recyclerView = view.findViewById(R.id.recyler_view_Arama);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arama_bar = view.findViewById(R.id.edt_arama_bar);

        mKullaniciler = new ArrayList<>();
        kullaniciAdapter = new KullaniciAdapter(getContext(),mKullaniciler);

        recyclerView.setAdapter(kullaniciAdapter);

        kullanicileriOku();

        arama_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                kullaniciAra(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void kullaniciAra(String s)
    {
        Query sorgu = FirebaseDatabase.getInstance().getReference("Kullanicilar").orderByChild("kullaniciadi")
                .startAt(s)
                .endAt(s+"\uf8ff");

        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mKullaniciler.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    mKullaniciler.add(kullanici);
                }

                kullaniciAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kullanicileriOku()
    {
        DatabaseReference kullanicilerYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar");

        kullanicilerYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(arama_bar.getText().toString().equals(""))
                {
                    mKullaniciler.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Kullanici kullanici = snapshot.getValue(Kullanici.class);
                        mKullaniciler.add(kullanici);
                    }
                    kullaniciAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
