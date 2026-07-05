package com.lhavanguane.tisimu.ui.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.SongComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongCommentAdapter extends RecyclerView.Adapter<SongCommentAdapter.CommentViewHolder> {

    private final List<SongComment> displayList = new ArrayList<>();
    private OnCommentActionListener actionListener;
    private String currentUserId;

    public interface OnCommentActionListener {
        void onLikeClick(SongComment comment);
        void onReplyClick(SongComment comment);
        void onDeleteClick(SongComment comment);
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.actionListener = listener;
    }

    public void setCurrentUserId(String uid) {
        this.currentUserId = uid;
    }

    public void setComments(List<SongComment> flat) {
        displayList.clear();

        // Separate top-level from replies
        List<SongComment> topLevel = new ArrayList<>();
        Map<String, List<SongComment>> repliesMap = new HashMap<>();

        for (SongComment c : flat) {
            if (c.getParentCommentId() == null) {
                topLevel.add(c);
            } else {
                String parentId = c.getParentCommentId();
                if (!repliesMap.containsKey(parentId)) {
                    repliesMap.put(parentId, new ArrayList<>());
                }
                repliesMap.get(parentId).add(c);
            }
        }

        // Build display list: top-level comment then its replies immediately after
        for (SongComment parent : topLevel) {
            displayList.add(parent);
            List<SongComment> replies = repliesMap.get(parent.getId());
            if (replies != null) {
                displayList.addAll(replies);
            }
        }

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
        SongComment comment = displayList.get(position);
        boolean isReply = comment.getParentCommentId() != null;
        holder.bind(comment, isReply);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
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

        void bind(SongComment comment, boolean isReply) {
            tvUserName.setText(comment.getUserName() != null ? comment.getUserName() : "");
            tvCommentText.setText(comment.getText() != null ? comment.getText() : "");
            tvLikeCount.setText(comment.getLikesCount() + " " +
                    itemView.getContext().getString(R.string.likes));

            // Time formatting
            if (comment.getCreatedAt() != null) {
                CharSequence ago = DateUtils.getRelativeTimeSpanString(
                        comment.getCreatedAt().getTime(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS);
                tvTimeAgo.setText(ago);
            } else {
                tvTimeAgo.setText("");
            }

            // Replies don't show a reply button (no nested nesting)
            tvReply.setVisibility(isReply ? View.GONE : View.VISIBLE);

            // Indent replies visually
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            int indent = isReply ? dpToPx(32) : 0;
            params.setMarginStart(indent);
            itemView.setLayoutParams(params);

            tvLikeCount.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onLikeClick(comment);
            });

            tvReply.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onReplyClick(comment);
            });

            // Allow delete for own comments (long press)
            itemView.setOnLongClickListener(v -> {
                if (currentUserId != null && currentUserId.equals(comment.getUserId())) {
                    if (actionListener != null) actionListener.onDeleteClick(comment);
                    return true;
                }
                return false;
            });
        }

        private int dpToPx(int dp) {
            float density = itemView.getContext().getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }
    }
}
