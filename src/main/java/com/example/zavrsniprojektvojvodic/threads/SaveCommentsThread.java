package com.example.zavrsniprojektvojvodic.threads;

import com.example.zavrsniprojektvojvodic.domain.CommentRecord;


import java.util.List;

public class SaveCommentsThread extends SavingToDatabaseThread implements Runnable {
    private List<CommentRecord> commentsList;

    public SaveCommentsThread(List<CommentRecord> commentsList) {
        this.commentsList = commentsList;
    }

    @Override
    public void run() {
        super.saveComments(commentsList);
    }
}
