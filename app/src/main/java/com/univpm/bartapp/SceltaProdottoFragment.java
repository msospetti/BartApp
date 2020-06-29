package com.univpm.bartapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SceltaProdottoFragment extends Fragment {

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private MyAdapterScelta adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    ArrayList<Oggetto> arrayList;
    private FirebaseUser currentUser;
    private String idOggettoScelto;
    private TextView prezzo, nome, nome_venditore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("oggetti");
        databaseReference.keepSynced(true);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        arrayList = new ArrayList<Oggetto>();
        recuperaDati();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_offerta, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("oggetti");
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        final String utente = mAuth.getUid();

        FirebaseRecyclerOptions<Oggetto> options = new FirebaseRecyclerOptions.Builder<Oggetto>()
                .setQuery(databaseReference.orderByChild("idUser").equalTo(utente), Oggetto.class)
                .build();

        idOggettoScelto = getArguments().getString("oggetto");
        adapter = new MyAdapterScelta(options, getContext(), idOggettoScelto);
        recyclerView.setAdapter(adapter);

        //Dati oggetto
        nome = (TextView) view.findViewById(R.id.nome_oggetto_desiderato);
        prezzo = (TextView) view.findViewById(R.id.prezzo_oggetto_desiderato);
        nome_venditore = (TextView) view.findViewById(R.id.nome_venditore_oggetto);
        databaseReference.child(idOggettoScelto.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nome.setText(dataSnapshot.child("nome").getValue().toString());
                prezzo.setText(dataSnapshot.child("prezzo").getValue().toString());
                nome_venditore.setText(dataSnapshot.child("nomeVenditore").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("a", "listener canceled", databaseError.toException());
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        //firebaseRecyclerAdapter.stopListening(); è da implementare
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void recuperaDati() {
        idOggettoScelto = getArguments().getString("oggetto");
    }
}
