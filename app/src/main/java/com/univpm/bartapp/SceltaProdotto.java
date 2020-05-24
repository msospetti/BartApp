package com.univpm.bartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SceltaProdotto extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<Oggetto> options;
    private FirebaseRecyclerAdapter<Oggetto, FirebaseViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    ArrayList<Oggetto> arrayList;
    String nome;
    String venditore;
    String prezzo;
    String idUser;

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scelta_prodotto);

        nome = getIntent().getStringExtra("nome");
        venditore = getIntent().getStringExtra("nomevend");
        prezzo = getIntent().getStringExtra("prezzo");
        idUser = getIntent().getStringExtra("idUser");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String utente = mAuth.getUid();
        arrayList = new ArrayList<Oggetto>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("oggetti");
        databaseReference.keepSynced(true);
        options = new FirebaseRecyclerOptions.Builder<Oggetto>().setQuery(databaseReference.orderByChild("idUser").equalTo(utente), Oggetto.class).build();
        Log.i("a", "SONO QUIiiiiiii");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_scelta);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    public void fetch() {
        adapter = new FirebaseRecyclerAdapter<Oggetto, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FirebaseViewHolder firebaseViewHolder, final int i, @NonNull final Oggetto oggetto) {

                firebaseViewHolder.nome.setText(oggetto.getNome());
                Log.i("a", "SONO QUI3");
                firebaseViewHolder.nomeVenditore.setText(oggetto.getNomeVenditore());
                firebaseViewHolder.prezzo.setText(String.valueOf(oggetto.getPrezzo()));
                firebaseViewHolder.idUser.setText(oggetto.getIdUser());

                firebaseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView nome1 = v.findViewById(R.id.nome_oggetto);
                        String Nome1 = nome1.getText().toString();
                        TextView nomeVend = v.findViewById(R.id.nome_venditore);
                        String NomeVend = nomeVend.getText().toString();
                        TextView prezzo1 = v.findViewById(R.id.prezzo);
                        String Prezzo1 = prezzo1.getText().toString();
                        TextView idUser1 = v.findViewById(R.id.id_venditore);
                        String IdUser1 = idUser1.getText().toString();

                        Intent intent = new Intent(v.getContext(), VisualizzaProdotto.class);
                        //intent.putExtra("descrizione", descrizione);
                        //intent.putExtra("nomeVend", nomeVend);

                        intent.putExtra("Nome1", nome);
                        intent.putExtra("NomeVend", venditore);
                        intent.putExtra("Prezzo1", prezzo);
                        intent.putExtra("idUser", idUser);
                        intent.putExtra("nome_offerta", Nome1);
                        intent.putExtra("nome_venditore", NomeVend);
                        intent.putExtra("prezzo_offerta", Prezzo1);
                        intent.putExtra("idUser", IdUser1);
                        startActivity(intent);

                    }
                });


            }


            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(SceltaProdotto.this).inflate(R.layout.rv_row, parent, false));
            }



        };
        recyclerView.setAdapter(adapter);

    }
}
