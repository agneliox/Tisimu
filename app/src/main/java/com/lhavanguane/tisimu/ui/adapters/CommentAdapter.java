package com.lhavanguane.tisimu.ui.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.entities.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private List<Comment> comments = new ArrayList<>();
    private OnCommentActionListener actionListener;

    public interface OnCommentActionListener {
        void onLikeClick(Comment comment);
        void onReplyClick(Comment comment);
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.actionListener = listener;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvTimeAgo;
        private final TextView tvCommentText;
        private final TextView tvLikeCount;
        private final TextView tvReply;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvReply = itemView.findViewById(R.id.tvReply);
        }

        void bind(Comment comment) {
            tvUserName.setText(comment.getUserName());
            tvCommentText.setText(comment.getText());
            tvLikeCount.setText(comment.getLikesCount() + " likes");

            // Format time
            long now = System.currentTimeMillis();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    comment.getCreatedAt(), now, DateUtils.MINUTE_IN_MILLIS);
            tvTimeAgo.setText(timeAgo);

            tvLikeCount.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onLikeClick(comment);
                }
            });

            tvReply.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onReplyClick(comment);
                }
            });
        }
    }
}
