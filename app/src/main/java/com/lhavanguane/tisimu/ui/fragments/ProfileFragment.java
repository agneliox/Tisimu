package com.lhavanguane.tisimu.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import com.lhavanguane.tisimu.BuildConfig;
import com.lhavanguane.tisimu.ui.activities.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.ui.activities.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private MaterialButton btnLogout;
    private TextView tvAppVersion;
    private View layoutEditProfile;
    private View layoutChangePassword;

    private FirebaseAuth mAuth;

    private Toolbar toolbar;
    private CircleImageView profile_pic;
    private TextView profile_name, profile_email, member_since;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(requireActivity());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews(view);
        setupToolbar();
        setupUserInfo();
        setupClickListeners();
        setupVersionInfo();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.profileToolbar);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvAppVersion = view.findViewById(R.id.tvAppVersion);
        profile_pic = view.findViewById(R.id.user_pic);
        profile_name = view.findViewById(R.id.profile_name);
        profile_email = view.findViewById(R.id.profile_email);
        member_since = view.findViewById(R.id.member_since);
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
    }

    private void setupToolbar() {
        if (getActivity() != null) {
            ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_menu_2);
            toolbar.setNavigationOnClickListener(v -> {
                DrawerLayout drawerLayout = ((MainActivity) requireActivity()).getDrawerLayout();
                if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });
        }
    }
    private void setupUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Check if user signed in with Google
            for (UserInfo profile : user.getProviderData()) {
                if ("google.com".equals(profile.getProviderId())) {
                    // User signed in with Google
//                    tvSignInMethod.setText("Connected with Google");
                    break;
                }
            }
        }

        if (user != null) {
            // Set user name
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                profile_name.setText(displayName);
            } else {
                String email = user.getEmail();
                if (email != null) {
                    String nameFromEmail = email.split("@")[0];
                    profile_name.setText(nameFromEmail);
                } else {
                    profile_name.setText(R.string.guest_user);
                }
            }

            // Set user email
            profile_email.setText(user.getEmail() != null ? user.getEmail() : "No email");

            // Set member since (using user creation time if available)
            if (user.getMetadata() != null && user.getMetadata().getCreationTimestamp() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                String date = sdf.format(new Date(user.getMetadata().getCreationTimestamp()));
                member_since.setText(getString(R.string.joined_since, date));
                Toast.makeText(requireContext(), "URL " + user.getPhotoUrl(), Toast.LENGTH_SHORT).show();
            } else {
                member_since.setText(R.string.member_since);
            }

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(profile_pic);

        }
    }




    private void setupClickListeners() {
        layoutEditProfile.setOnClickListener(v -> showEditProfileDialog());
        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> logout());
    }

    // ==================== EDIT PROFILE ====================
    private void showEditProfileDialog() {
        // TODO: Implement edit profile
        Toast.makeText(requireContext(), "Edit Profile coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showChangePasswordDialog() {
        // TODO: Implement change password via Firebase
        Toast.makeText(requireContext(), "Change Password coming soon", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
        private void setupVersionInfo() {
        tvAppVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to profile
        setupUserInfo();
//        setupLanguageDisplay();
    }
}