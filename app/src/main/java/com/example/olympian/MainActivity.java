package com.example.olympian;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import java.io.IOException;



public class MainActivity extends AppCompatActivity{
    private Button btnLogout,btnAtrasmusica,btnAdelantemusica,btnPausarmusica;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private RutinaAdapter rutinaAdapter;
    private ArrayList<Rutina> rutinaList;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private TextView textotitulo;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        btnAtrasmusica = findViewById(R.id.btnantes);
        btnPausarmusica = findViewById(R.id.btnpausar);
        btnAdelantemusica = findViewById(R.id.btndespues);
        textotitulo = findViewById(R.id.txtmusictitulo);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        textotitulo.setText("Another one bite the dust");
        String[] urlsCanciones = {
                "android.resource://" + getPackageName() + "/" + R.raw.another_one_bites_the_dust,
                "android.resource://" + getPackageName() + "/" + R.raw.eye_of_the_tiger,
                "android.resource://" + getPackageName() + "/" + R.raw.i_want_to_break_free,
                "android.resource://" + getPackageName() + "/" + R.raw.mi_gente,
                "android.resource://" + getPackageName() + "/" + R.raw.somebody_to_love,
                "android.resource://" + getPackageName() + "/" + R.raw.till_i_collapse,
                "android.resource://" + getPackageName() + "/" + R.raw.under_pressure
        };
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        try {
            mediaPlayer.setDataSource(urlsCanciones[0]);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnPausarmusica.setBackgroundResource(R.drawable.unpause);
        btnAdelantemusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongIndex++;
                if (currentSongIndex >= urlsCanciones.length) {
                    currentSongIndex = 0; // Volver al inicio del array
                }
                cambiarCancion(urlsCanciones[currentSongIndex]);
            }
        });
        btnAtrasmusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongIndex--;
                if (currentSongIndex < 0) {
                    currentSongIndex = urlsCanciones.length - 1;
                }
                cambiarCancion(urlsCanciones[currentSongIndex]);
            }
        });
        btnPausarmusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    // Cambiar el icono a "pausar.png"
                    btnPausarmusica.setBackgroundResource(R.drawable.pausar);
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    // Cambiar el icono a otro recurso de imagen
                    btnPausarmusica.setBackgroundResource(R.drawable.unpause);
                    mediaPlayer.start();
                    isPlaying = true;
                }
            }
        });
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
    private void cambiarCancion(String urlCancion) {
        try {
            if(currentSongIndex==0){
                textotitulo.setText("Another one bite the dust");
            }
            if(currentSongIndex==1){
                textotitulo.setText("Eye of the tiger");
            }
            if(currentSongIndex==2){
                textotitulo.setText("I want to break free");
            }
            if(currentSongIndex==3){
                textotitulo.setText("Mi gente");
            }
            if(currentSongIndex==4){
                textotitulo.setText("Somebody to love");
            }
            if(currentSongIndex==5){
                textotitulo.setText("Till I collapse");
            }
            if(currentSongIndex==6){
                textotitulo.setText("Under pressure");
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(urlCancion);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
