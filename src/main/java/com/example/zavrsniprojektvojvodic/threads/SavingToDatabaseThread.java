package com.example.zavrsniprojektvojvodic.threads;

import com.example.zavrsniprojektvojvodic.domain.CommentRecord;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;

import java.util.List;

public abstract class SavingToDatabaseThread{
    public static Boolean isDatabaseOperationInProgress = false;

    public synchronized void saveComments(List<CommentRecord> commentList) {

        while(isDatabaseOperationInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        isDatabaseOperationInProgress = true;

        DatabaseUtils.saveCommentsRecords(commentList);

        isDatabaseOperationInProgress = false;

        notifyAll();
    }

    public synchronized List<CommentRecord> readFromDataBase()
    {
        while(isDatabaseOperationInProgress)
        {
            try{
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        isDatabaseOperationInProgress = true;

        List<CommentRecord> commentList = DatabaseUtils.getCommentsRecord();

        isDatabaseOperationInProgress = false;
        notifyAll();

        return commentList;
    }

}
