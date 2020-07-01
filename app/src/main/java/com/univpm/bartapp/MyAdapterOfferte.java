package com.univpm.bartapp;


import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestoreDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapterOfferte extends FirestoreRecyclerAdapter<Offerta, MyAdapterOfferte.FirestoreViewHolder> {

    private ArrayList<Offerta> offerta;
    private DatabaseReference databaseReference;
    private String idProdAcq;
    private String idProdVend;
    private String idAcq;
    private String idVend;
    Context context;

    public MyAdapterOfferte(@NonNull FirestoreRecyclerOptions<Offerta> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final FirestoreViewHolder viewHolder, int position, @NonNull Offerta offerta) {

        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        storageReference.child("Image").child("ImmaginiOggetti").child(offerta.getIdVend()).child(offerta.getNomeOggettoVend()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(viewHolder.immagineOggettoAcq);
            }
        });

        storageReference.child("Image").child("ImmaginiOggetti").child(offerta.getIdAcq()).child(offerta.getNomeOggettoAcq()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(viewHolder.immagineOggettoVend);
            }
        });

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String a = documentSnapshot.getId();
        viewHolder.nomeAcq.setText(offerta.getNomeVend());
        viewHolder.nomeVend.setText(offerta.getNomeAcq());
        viewHolder.nomeOggettoAcq.setText(offerta.getNomeOggettoVend());
        viewHolder.nomeOggettoVend.setText(offerta.getNomeOggettoAcq());
        viewHolder.prezzoAcq.setText(offerta.getPrezzoOggettoVend());
        viewHolder.prezzoVend.setText(offerta.getPrezzoAcq());

        idProdAcq = offerta.getIdProdAcq();
        idProdVend = offerta.getIdProdVend();
        Log.i("PRODOTTO VERDE" , idProdVend);

        final Long keyId = this.getItemId(position);

        viewHolder.btnRifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rifiuta(a);
            }
        });
        viewHolder.btnAccetta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accetta(a, idProdAcq, idProdVend);
            }
        });
    }

    @NonNull
    @Override
    public FirestoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdapterOfferte.FirestoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_offerte, parent, false));
    }

    static class FirestoreViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeOggettoVend, nomeOggettoAcq;
        public TextView prezzoVend, prezzoAcq;
        public TextView nomeAcq, nomeVend;
        public ImageView immagineOggettoVend, immagineOggettoAcq;
        public Button btnAccetta, btnRifiuta;

        public FirestoreViewHolder(@NonNull View v) {
            super(v);
            nomeOggettoAcq = (TextView) v.findViewById(R.id.nome_oggetto);
            nomeOggettoVend = (TextView) v.findViewById(R.id.nome_oggetto_vend);
            prezzoAcq = (TextView) v.findViewById(R.id.prezzo);
            prezzoVend = (TextView) v.findViewById(R.id.prezzo_mio);
            nomeAcq = (TextView) v.findViewById(R.id.nome_venditore);
            nomeVend = (TextView) v.findViewById(R.id.nome_mio);
            immagineOggettoVend = (ImageView) v.findViewById(R.id.immagine_mio_oggetto);
            immagineOggettoAcq = (ImageView) v.findViewById(R.id.immagine_oggetto);
            btnAccetta = (Button) v.findViewById(R.id.btn_accetta);
            btnRifiuta = (Button) v.findViewById(R.id.btn_rifiuta);
        }

    }

    public void rifiuta(final String keyId) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Attenzione!");
        dialog.setCancelable(false);
        dialog.setMessage("Sei sicuro di voler rifiutare l'offerta?");
        dialog.setPositiveButton("Rifiuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //Query query=db.collection("scambi").orderBy(a);
                db.collection("scambi").document(keyId).delete();

            }
        });
        dialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void accetta(final String keyId, final String idProdAcq, final String idProdVend) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Attenzione!");
        dialog.setCancelable(false);
        dialog.setMessage("Sei sicuro di voler accettare l'offerta?");
        dialog.setPositiveButton("Accetta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                operazione1(idProdAcq);
                operazione2(idProdAcq);
                operazione3(idProdVend);
                //operazione4(idProdVend);
            }
        });
        dialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void operazione1 (String idProdAcq) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("scambi").whereEqualTo("idProdAcq", idProdAcq).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("ciao1 ", "ciao1");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("cascadsscciaoaciacoaico", document.getId() + "->" + document.getData());
                        document.getReference().delete();
                        Toast.makeText(context, "Scambio effettuato", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("errore" , "c'e stato un errore");
                }
            }
        });
    } //oggetto rosso corretto
    public void operazione2 (String idProdAcq) { //oggetto verde dell'altro
        FirebaseFirestore db0 = FirebaseFirestore.getInstance();
        db0.collection("scambi").whereEqualTo("idProdVend", idProdAcq).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("ciao2 ", "ciao2");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("cascadsscciaoaciacoaico", document.getId() + "->" + document.getData());
                        document.getReference().delete();
                        Toast.makeText(context, "Scambio effettuato", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("errore" , "c'e stato un errore");
                }
            }
        });

    } //oggetto verde dell'altro corretto
    public void operazione3 (String idProdVend) {
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        db1.collection("scambi").whereEqualTo("idProdAcq", idProdVend).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("ciao3", "ciao3");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("cascadsscciaoaciacoaico", document.getId() + "->" + document.getData());
                        document.getReference().delete();
                    }
                } else {
                    Log.i("errore" , "c'e stato un errore");
                }
            }
        });
    } //offerte che IO HO INVIATO ad un altro utente dello stesso oggetto
    // che scambio a causa di una offerta ricevuta
    /*public void operazione4 (String idProdVend) { //oggetto offerto
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("scambi").whereEqualTo("idProdVend", idProdVend).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("ciao4 ", "ciao4");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("cascadsscciaoaciacoaico", document.getId() + "->" + document.getData());
                        document.getReference().delete();
                        Toast.makeText(context, "Scambio effettuato", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("errore" , "c'e stato un errore");
                }
            }
        });
    }*/

}