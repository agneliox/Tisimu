package com.lhavanguane.tisimu.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lhavanguane.tisimu.R;

public class SongDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_song_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int songId = getIntent().getIntExtra("SONG_ID", -1);
        String songTitle = getIntent().getStringExtra("SONG_TITLE");

        TextView tvTitle = findViewById(R.id.tvSongTitle);
        tvTitle.setText(songTitle);
    }
}