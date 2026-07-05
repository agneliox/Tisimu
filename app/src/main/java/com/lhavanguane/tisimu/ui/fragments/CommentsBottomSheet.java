package com.lhavanguane.tisimu.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.SongComment;
import com.lhavanguane.tisimu.services.SongFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.SongCommentAdapter;

import java.util.List;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SONG_ID = "songId";

    private String songId;
    private String replyingToCommentId = null;

    private SongFirestoreManager songManager;
    private SongCommentAdapter commentAdapter;
    private ListenerRegistration commentsListener;

    private RecyclerView rvComments;
    private TextInputEditText etComment;
    private MaterialButton btnPostComment;
    private TextView tvReplyingTo;

    public static CommentsBottomSheet newInstance(String songId) {
        CommentsBottomSheet sheet = new CommentsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SONG_ID, songId);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songId = getArguments().getString(ARG_SONG_ID);
        }
        songManager = SongFirestoreManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvComments = view.findViewById(R.id.rvComments);
        etComment = view.findViewById(R.id.etComment);
        btnPostComment = view.findViewById(R.id.btnPostComment);
        tvReplyingTo = view.findViewById(R.id.tvReplyingTo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user != null ? user.getUid() : null;

        commentAdapter = new SongCommentAdapter();
        commentAdapter.setCurrentUserId(currentUserId);
        commentAdapter.setOnCommentActionListener(new SongCommentAdapter.OnCommentActionListener() {
            @Override
            public void onLikeClick(SongComment comment) {
                if (currentUserId == null) {
                    showToast(getString(R.string.sign_in_to_interact));
                    return;
                }
                songManager.toggleCommentLike(songId, comment.getId(),
                        new SongFirestoreManager.VoidCallback() {
                            @Override public void onSuccess() {}
                            @Override public void onFailure(Exception e) {
                                showToast(getString(R.string.error_generic));
                            }
                        });
            }

            @Override
            public void onReplyClick(SongComment comment) {
                if (currentUserId == null) {
                    showToast(getString(R.string.sign_in_to_interact));
                    return;
                }
                setReplyingTo(comment);
            }

            @Override
            public void onDeleteClick(SongComment comment) {
                showDeleteConfirmation(comment);
            }
        });

        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setAdapter(commentAdapter);

        btnPostComment.setOnClickListener(v -> postComment(currentUserId));

        // Dismiss replying-to banner when user clears the field
        tvReplyingTo.setOnClickListener(v -> clearReplyState());

        startListener();
    }

    private void startListener() {
        commentsListener = songManager.listenToComments(songId,
                new SongFirestoreManager.CommentsCallback() {
                    @Override
                    public void onSuccess(List<SongComment> comments) {
                        commentAdapter.setComments(comments);
                        if (comments.isEmpty() && rvComments != null) {
                            rvComments.scrollToPosition(0);
                        } else if (!comments.isEmpty()) {
                            rvComments.smoothScrollToPosition(comments.size() - 1);
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        showToast(getString(R.string.error_generic));
                    }
                });
    }

    private void postComment(String currentUserId) {
        if (currentUserId == null) {
            showToast(getString(R.string.sign_in_to_interact));
            return;
        }
        String text = etComment.getText() != null ? etComment.getText().toString().trim() : "";
        if (text.isEmpty()) return;

        btnPostComment.setEnabled(false);
        songManager.addComment(songId, text, replyingToCommentId,
                new SongFirestoreManager.VoidCallback() {
                    @Override public void onSuccess() {
                        if (etComment != null) etComment.setText("");
                        clearReplyState();
                        if (btnPostComment != null) btnPostComment.setEnabled(true);
                    }
                    @Override public void onFailure(Exception e) {
                        showToast(getString(R.string.error_generic));
                        if (btnPostComment != null) btnPostComment.setEnabled(true);
                    }
                });
    }

    private void setReplyingTo(SongComment comment) {
        replyingToCommentId = comment.getId();
        tvReplyingTo.setText(getString(R.string.replying_to, comment.getUserName()));
        tvReplyingTo.setVisibility(View.VISIBLE);
        if (etComment != null) etComment.requestFocus();
    }

    private void clearReplyState() {
        replyingToCommentId = null;
        tvReplyingTo.setVisibility(View.GONE);
    }

    private void showDeleteConfirmation(SongComment comment) {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.delete_comment_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    boolean isTopLevel = comment.getParentCommentId() == null;
                    songManager.deleteComment(songId, comment.getId(), isTopLevel,
                            new SongFirestoreManager.VoidCallback() {
                                @Override public void onSuccess() {}
                                @Override public void onFailure(Exception e) {
                                    showToast(getString(R.string.error_generic));
                                }
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showToast(String msg) {
        if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (commentsListener != null) commentsListener.remove();
    }
}
