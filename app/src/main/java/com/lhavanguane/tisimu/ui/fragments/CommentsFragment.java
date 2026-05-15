package com.lhavanguane.tisimu.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.ui.adapters.CommentAdapter;
import com.lhavanguane.tisimu.viewmodels.SongDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvComments;
    private TextInputEditText etComment;
    private MaterialButton btnPostComment;
    private SongDetailViewModel viewModel;
    private CommentAdapter commentAdapter;

    public CommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentsFragment newInstance(String param1, String param2) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        rvComments = view.findViewById(R.id.rvComments);
        etComment = view.findViewById(R.id.etComment);
        btnPostComment = view.findViewById(R.id.btnPostComment);

        viewModel = new ViewModelProvider(requireActivity()).get(SongDetailViewModel.class);

        setupRecyclerView();
        observeData();
        setupListeners();

        return view;
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setAdapter(commentAdapter);

        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onLikeClick(com.lhavanguane.tisimu.data.database.entities.Comment comment) {
                viewModel.likeComment(comment);
                Toast.makeText(requireContext(), R.string.liked, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReplyClick(com.lhavanguane.tisimu.data.database.entities.Comment comment) {
                etComment.setText("@" + comment.getUserName() + " ");
                etComment.requestFocus();
            }
        });
    }

    private void observeData() {
        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments != null) {
                commentAdapter.setComments(comments);
            }
        });
    }

    private void setupListeners() {
        btnPostComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                String userName = FirebaseAuth.getInstance().getCurrentUser() != null ?
                        FirebaseAuth.getInstance().getCurrentUser().getEmail() : "Anonymous";
                viewModel.postComment(commentText, userName);
                etComment.setText("");
                Toast.makeText(requireContext(), R.string.comment_posted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.please_enter_comment, Toast.LENGTH_SHORT).show();
            }
        });
    }
}