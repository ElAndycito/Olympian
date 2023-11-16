package com.example.olympian;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olympian.adapter.RutinaAdapter;
import com.example.olympian.model.Rutina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity{
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private RutinaAdapter rutinaAdapter;
    private ArrayList<Rutina> rutinaList;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            mAuth.signOut();
                            finish();
                        }
                    }
                };
                mAuth.addAuthStateListener(authStateListener);
                checkUserAndRedirect();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        checkUserAndRedirect();
    }

    private void checkUserAndRedirect() {
        db = FirebaseFirestore.getInstance();

        rutinaList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rutinaAdapter = new RutinaAdapter(rutinaList);
        recyclerView.setAdapter(rutinaAdapter);
        cargarRutinasDesdeFirestore();}

    private void cargarRutinasDesdeFirestore() {
        db.collection("rutinas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rutinaList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Rutina rutina = document.toObject(Rutina.class);
                            rutinaList.add(rutina);
                        }

                        rutinaAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

}
