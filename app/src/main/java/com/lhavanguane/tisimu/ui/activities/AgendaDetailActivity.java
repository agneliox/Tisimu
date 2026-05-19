package com.lhavanguane.tisimu.ui.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.AgendaItem;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AgendaDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private Chip chipDate;
    private Chip chipCreatedBy;
    private TextView tvTitle;
    private TextView tvContent;
    private MaterialButton btnBack;

    private AgendaItem agendaItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agenda_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_agenda_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get agenda item from intent
        agendaItem = (AgendaItem) getIntent().getSerializableExtra("AGENDA_ITEM");

        if (agendaItem == null) {
            Toast.makeText(this, "Agenda item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        displayAgendaContent();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        chipDate = findViewById(R.id.chipDate);
        chipCreatedBy = findViewById(R.id.chipCreatedBy);
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        btnBack = findViewById(R.id.btnBack);

        // Make content scrollable
        tvContent.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void displayAgendaContent() {
        // Set title
        tvTitle.setText(agendaItem.getTitle());

        // Set content
        tvContent.setText(agendaItem.getContent());

        // Set date chip
        if (agendaItem.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
            String dateStr = sdf.format(agendaItem.getCreatedAt());
            chipDate.setText(dateStr);
        } else {
            chipDate.setVisibility(android.view.View.GONE);
        }

        // Set created by chip
        String createdByText = "Added by " + agendaItem.getCreatedByUserName();
        chipCreatedBy.setText(createdByText);

        // Set toolbar title
        toolbar.setTitle(agendaItem.getTitle());
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}