package com.example.apkproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView songList;
    private Button playButton, favoriteButton;
    private EditText searchEditText;
    private MediaPlayer mediaPlayer;
    private String[] allSongs = {"limpbizkit_my_generation", "linkinpark_lost", "linkinpark_numb"};
    private List<String> favoriteSongs = new ArrayList<>();
    private int currentSongIndex = -1;
    private ArrayAdapter<String> adapter;
    private boolean showFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = findViewById(R.id.songList);
        playButton = findViewById(R.id.playButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        searchEditText = findViewById(R.id.searchEditText);

        updateList();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateList(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        songList.setOnItemClickListener((parent, view, position, id) -> {
            currentSongIndex = position;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            String song = adapter.getItem(position);
            int resId = getResources().getIdentifier(song, "raw", getPackageName());
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId);
                if (mediaPlayer != null) {
                    Toast.makeText(this, "Выбрана: " + song, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ошибка создания MediaPlayer", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Файл " + song + " не найден", Toast.LENGTH_SHORT).show();
            }
        });

        playButton.setOnClickListener(v -> {
            if (currentSongIndex != -1 && mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    Toast.makeText(this, "Воспроизведение начато", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.pause();
                    Toast.makeText(this, "Пауза", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Выберите песню или файл отсутствует", Toast.LENGTH_SHORT).show();
            }
        });


        favoriteButton.setOnClickListener(v -> {
            showFavorites = !showFavorites;
            updateList();
            favoriteButton.setText(showFavorites ? "Все песни" : "Избранное");
        });

        songList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (adapter != null && position >= 0 && position < adapter.getCount()) {
                String song = adapter.getItem(position);
                if (!favoriteSongs.contains(song)) {
                    favoriteSongs.add(song);
                    Toast.makeText(this, song + " добавлено в избранное", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, song + " уже в избранном", Toast.LENGTH_SHORT).show();
                }
                updateList();
                return true;
            }
            return false;
        });
    }

    private void updateList() {
        List<String> displaySongs = new ArrayList<>();
        String searchText = searchEditText.getText().toString().toLowerCase();

        if (showFavorites) {
            if (favoriteSongs.isEmpty()) {
                displaySongs.add("Нет избранных песен");
            } else {
                displaySongs.addAll(favoriteSongs);
            }
        } else {
            for (String song : allSongs) {
                if (searchText.isEmpty() || song.toLowerCase().contains(searchText)) {
                    displaySongs.add(song);
                }
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displaySongs);
        songList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}