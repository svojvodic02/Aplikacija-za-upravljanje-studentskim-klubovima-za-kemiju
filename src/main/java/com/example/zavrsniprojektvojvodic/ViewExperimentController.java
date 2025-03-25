package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.threads.GettingCommentsFromDataBaseThread;
import com.example.zavrsniprojektvojvodic.threads.SaveCommentsThread;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewExperimentController {

    List<Student> authors=new ArrayList<>();
    Set<String> privleges=new HashSet<>();
    LocalDate date;
    List<Student> allStudents=DatabaseUtils.getStudents();
    UserSession currentUser=UserSession.getInstace("","",privleges,"");
    CurrentExperiment currentExperimentBeingViewed=CurrentExperiment.getInstace("",0l,
            authors,date,date,"",0);
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField startDateTextField;
    @FXML
    private TextField endDateTextField;
    @FXML
    private ListView authorsListView;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private TextField addNotesTextField;
    @FXML
    private TableView<CommentRecord> addNotesTextAreaTableView;
    @FXML
    private TableColumn<CommentRecord,String> userNameWhoCommentedTableColumn;
    @FXML
    private TableColumn<CommentRecord,String> commentTableColumn;

    @FXML
    void initialize(){

        userNameWhoCommentedTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CommentRecord, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CommentRecord, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().id().toString());
            }
        });
        commentTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CommentRecord, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CommentRecord, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().comment());
            }
        });

        nameTextField.setEditable(false);
        nameTextField.setText(currentExperimentBeingViewed.getName());

        startDateTextField.setEditable(false);
        startDateTextField.setText(currentExperimentBeingViewed.getDateStarted().toString());

        if(currentExperimentBeingViewed.getDateFinished()!=null){
            endDateTextField.setEditable(false);
            endDateTextField.setText(currentExperimentBeingViewed.getDateFinished().toString());
        }else if(currentExperimentBeingViewed.getDateFinished()==null){
            endDateTextField.setText(" ");
        }

        List<String> namesOfStudents=DatabaseUtils.getStudentName(currentExperimentBeingViewed.getAuthors());
        ObservableList<String> studentObservableList= FXCollections.observableArrayList(namesOfStudents);
        authorsListView.setItems(studentObservableList);

        notesTextArea.setEditable(false);
        notesTextArea.setText(currentExperimentBeingViewed.getNotes());

        GettingCommentsFromDataBaseThread gettingCommentsFromDataBaseThread = new GettingCommentsFromDataBaseThread();
        Thread starter = new Thread(gettingCommentsFromDataBaseThread);
        starter.start();
        try
        {starter.join();
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        //*List<Comment> newCommentList=gettingCommentsFromDataBaseThread.getCommentList();
        List<CommentRecord> newCommentList=gettingCommentsFromDataBaseThread.getCommentList();

        ObservableList<CommentRecord> newCommentListObservableList=FXCollections.observableArrayList(newCommentList);

        addNotesTextAreaTableView.setItems(newCommentListObservableList);

        Timeline refreshThread=new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addNotesTextAreaTableView.setItems(newCommentListObservableList);
            }
        }),new KeyFrame(Duration.seconds(1)));
        refreshThread.setCycleCount(Animation.INDEFINITE);
        refreshThread.play();
    }

    public void addNotes(){

        if(!addNotesTextField.getText().isEmpty()){

            Long newCommentId=DatabaseUtils.getNextCommentId();

            Optional<Student> currentStudent=Optional.empty();

            for(int i=0;i<allStudents.size();i++){
                if(allStudents.get(i).getOIB().equals(currentUser.getOIB())){
                    currentStudent=Optional.of(allStudents.get(i));
                }
            }

            CommentRecord newComment = new CommentRecord(newCommentId, addNotesTextField.getText(),  currentStudent.get().getId(),
                    currentExperimentBeingViewed.getId());


            List<CommentRecord> commentList = new ArrayList<>();

            commentList.add(newComment);
            SaveCommentsThread saveCommentsThread = new SaveCommentsThread(commentList);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(saveCommentsThread);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saving complete");
            alert.setHeaderText("New comment succesfully saved!");
            alert.setContentText( currentStudent.get().getName() + " has added a comment!");

            alert.showAndWait();
        }

    }

    public void goBackToAllExperiments(){

        currentExperimentBeingViewed=null;

        List<String> privligesList=currentUser.getPrivileges().stream().toList();

        for(int i=0;i<privligesList.size();i++){
            if(privligesList.get(i).contains("IS_ADMIN")){
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view-all-experiments.fxml"));
                try {
                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                    HelloApplication.getMainStage().setScene(scene);
                    HelloApplication.getMainStage().show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(privligesList.get(i).contains("IS_NOT_ADMIN")){
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-page.fxml"));
                try {
                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                    HelloApplication.getMainStage().setScene(scene);
                    HelloApplication.getMainStage().show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
