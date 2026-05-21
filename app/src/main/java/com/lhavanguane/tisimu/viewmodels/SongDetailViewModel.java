package com.lhavanguane.tisimu.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.lhavanguane.tisimu.entities.Comment;
import com.lhavanguane.tisimu.entities.MelodyProposal;
import com.lhavanguane.tisimu.entities.Song;
import com.lhavanguane.tisimu.entities.Suggestion;

import java.util.List;

public class SongDetailViewModel extends AndroidViewModel {
    private final TisimuDatabase database;
    private final MutableLiveData<Song> currentSong;
    private final MutableLiveData<List<Comment>> comments;
    private final MutableLiveData<List<MelodyProposal>> melodyProposals;

    private int currentSongId;

    public SongDetailViewModel(Application application) {
        super(application);
        database = TisimuDatabase.getInstance(application);
        currentSong = new MutableLiveData<>();
        comments = new MutableLiveData<>();
        melodyProposals = new MutableLiveData<>();
    }

    public void loadSong(int songId) {
        this.currentSongId = songId;

        // Load song in background
        new Thread(() -> {
            Song song = database.songDao().getSongByIdSync(songId);
            currentSong.postValue(song);
        }).start();

        loadComments();
        loadMelodyProposals();
    }

    private void loadComments() {
        new Thread(() -> {
            List<Comment> commentList = (List<Comment>) database.commentDao().getCommentsBySongId(currentSongId);
            comments.postValue(commentList);
        }).start();
    }

    private void loadMelodyProposals() {
        new Thread(() -> {
            List<MelodyProposal> proposals = (List<MelodyProposal>) database.melodyProposalDao().getMelodyProposalsBySongId(currentSongId);
            melodyProposals.postValue(proposals);
        }).start();
    }

    public void postComment(String commentText, String userName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";

        Comment comment = new Comment(currentSongId, userId, userName, commentText);

        new Thread(() -> {
            database.commentDao().insert(comment);
            loadComments(); // Refresh
        }).start();
    }

    public void likeComment(Comment comment) {
        new Thread(() -> {
            database.commentDao().likeComment(comment.getId());
            loadComments(); // Refresh
        }).start();
    }

    public void addMelodyProposal(String title, String type, String url, String description, String userName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";

        MelodyProposal proposal = new MelodyProposal(currentSongId, userId, userName, type, url, title);
        proposal.setDescription(description);

        new Thread(() -> {
            database.melodyProposalDao().insert(proposal);
            loadMelodyProposals(); // Refresh
        }).start();
    }

    public void likeMelody(MelodyProposal melody) {
        new Thread(() -> {
            database.melodyProposalDao().likeProposal(melody.getId());
            loadMelodyProposals(); // Refresh
        }).start();
    }

    public void submitSuggestion(int verseNumber, String currentText, String suggestedText, String justification) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";
        String userName = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getEmail() : "Anonymous";

        Suggestion suggestion = new Suggestion(currentSongId, userId, userName,
                verseNumber, currentText, suggestedText, justification);

        new Thread(() -> {
            database.suggestionDao().insert(suggestion);
        }).start();
    }

    public LiveData<Song> getSong() {
        return currentSong;
    }

    public LiveData<List<Comment>> getComments() {
        return comments;
    }

    public LiveData<List<MelodyProposal>> getMelodyProposals() {
        return melodyProposals;
    }
}
