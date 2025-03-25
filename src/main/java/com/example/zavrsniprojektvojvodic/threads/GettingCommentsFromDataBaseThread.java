package com.example.zavrsniprojektvojvodic.threads;

import com.example.zavrsniprojektvojvodic.domain.CommentRecord;

import java.util.List;

public class GettingCommentsFromDataBaseThread extends SavingToDatabaseThread implements Runnable{

    private List<CommentRecord> commentList;

    @Override
    public void run() {
        commentList = super.readFromDataBase();
    }

    public List<CommentRecord> getCommentList() {
        return commentList;
    }
}
