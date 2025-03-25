package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditExperimentController {
    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    List<Student> authors=new ArrayList<>();

    List<Student> allStudents=DatabaseUtils.getStudents();
    LocalDate date;
    CurrentExperiment currentExperimentBeingEdited=CurrentExperiment.getInstace("",0l,
            authors,date,date,"",0);

    Set<String> privleges=new HashSet<>();
    UserSession currentUser=UserSession.getInstace("","",privleges,"");
    CurrentExperiment currentExperimentBeingViewed=CurrentExperiment.getInstace("",0l,
            authors,date,date,"",0);


    @FXML
    private TextField editedExperimentNameTextField;
    @FXML
    private ListView<String> allstudentAuthorsListView;
    @FXML
    private ComboBox<String> studentNamesComboBox;
    @FXML
    private DatePicker editedExperimentStartingDatePicker;
    @FXML
    private DatePicker editedExperimentEndingDatePicker;
    @FXML
    private TextArea editedExperimentNotesTextArea;
    @FXML
    private TextArea currentExperimentBeingEditedTextArea;
    @FXML
    private TextField exitedExperimentProgressTextField;
    @FXML
    private Button addExperimentAuthorsButton;

    @FXML
    public void initialize(){

        List<Student> chosenExperimentAuthors= DatabaseUtils.getAuthorsOfExperiment(currentExperimentBeingEdited.getId());
        List<String> namesOfChosenClubExperiments=DatabaseUtils.getStudentName(chosenExperimentAuthors);

        List<String> listAllStudentsNames=DatabaseUtils.getStudentName(allStudents);
        ObservableList<String> allStudentsNamesObservableList=FXCollections.observableArrayList(listAllStudentsNames);
        ObservableList<String> observableListChosenExperimentAuthors= FXCollections.observableArrayList(namesOfChosenClubExperiments);
        allstudentAuthorsListView.setItems(observableListChosenExperimentAuthors);
        studentNamesComboBox.setItems(allStudentsNamesObservableList);

        currentExperimentBeingEditedTextArea.setText(currentExperimentBeingEdited.getName());
        currentExperimentBeingEditedTextArea.setEditable(false);
        editedExperimentStartingDatePicker.setEditable(false);
        editedExperimentEndingDatePicker.setEditable(false);

    }

    public void addNewMemberToList(){
        addExperimentAuthorsButton.setOnAction((e) -> {
            allstudentAuthorsListView.getItems().add(studentNamesComboBox.getSelectionModel().getSelectedItem());
        });
    }
    public void clearMemberList(){
        List<String> emptyStringList=new ArrayList<>();
        ObservableList<String> emptyList=FXCollections.observableArrayList(emptyStringList);
        allstudentAuthorsListView.setItems(emptyList);
    }

    public void saveEdit() throws SQLException, IOException {

        Experiment experimentEdited=new Experiment.Builder(currentExperimentBeingEdited.getName()).setId(currentExperimentBeingEdited.getId())
                .setAuthors(currentExperimentBeingEdited.getAuthors()).setDateStarted(currentExperimentBeingEdited.getDateStarted())
                .setDateFinished(currentExperimentBeingEdited.getDateFinished()).setNotes(currentExperimentBeingEdited.getNotes())
                .setProgress(currentExperimentBeingEdited.getProgress()).createExperiment();


        List<String> listNamesOfExperimentAuthors=allstudentAuthorsListView.getItems();

        List<Student> editedAuthors=new ArrayList<>();

            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Are you sure you want to edit " + currentExperimentBeingEdited.getName() + " experiment?");
            alert1.setHeaderText(null);
            alert1.setContentText("Choose your option.");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeTwo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert1.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
            alert1.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);

            Optional<ButtonType> result = alert1.showAndWait();
            if (result.get() == buttonTypeOne) {

                for (int i = 0; i < listNamesOfExperimentAuthors.size(); i++) {
                    for (int j = 0; j < allStudents.size(); j++) {
                        if (allStudents.get(j).getName().contains(listNamesOfExperimentAuthors.get(i))) {
                            editedAuthors.add(allStudents.get(j));
                        }
                    }
                }

                DatabaseUtils.deleteExperimentAuthors(currentExperimentBeingEdited.getId());


                if(!editedAuthors.isEmpty()) {
                    DatabaseUtils.saveExperimentAuthors(editedAuthors, currentExperimentBeingEdited.getId());
                }

                String editedExperimentName = editedExperimentNameTextField.getText();

                if (!editedExperimentName.isEmpty()) {
                    experimentEdited.setName(editedExperimentName);
                    DatabaseUtils.updateExperimentName(experimentEdited);

                }

                if(editedExperimentStartingDatePicker.getValue()!=null){
                    LocalDate experimentStartingDate = editedExperimentStartingDatePicker.getValue();
                    experimentEdited.setDateStarted(experimentStartingDate);
                    DatabaseUtils.updateExperimentStartingDate(experimentEdited);
                }
                if(editedExperimentEndingDatePicker.getValue()!=null){
                    LocalDate experimentEndingDate = editedExperimentStartingDatePicker.getValue();
                    experimentEdited.setDateFinished(experimentEndingDate);
                    DatabaseUtils.updateExperimentEndingDate(experimentEdited);
                }

                if(editedExperimentNotesTextArea!=null){
                    String editedNotes=editedExperimentNotesTextArea.getText();
                    experimentEdited.setNotes(editedNotes);
                    DatabaseUtils.updateExperimentNotes(experimentEdited);
                }
                if(exitedExperimentProgressTextField!=null){
                    String editedProgressString=exitedExperimentProgressTextField.getText();
                    Integer editedProgress=Integer.parseInt(editedProgressString);
                    experimentEdited.setProgress(editedProgress);
                    DatabaseUtils.updateExperimentProgress(experimentEdited);
                }

                Set<String> privleges=new HashSet<>();

                UserSession currentUser=UserSession.getInstace("","",privleges,"");

                List<Changes<String,String>> newChangesList=new ArrayList<>();
                Changes<String,String> newChanges=new Changes<String,String>(currentUser.getUserFirstName(),currentExperimentBeingEdited.getName(),currentTime,currentDate);
                newChangesList.add(newChanges);

                FileUtils.serializeChanges(newChangesList);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Editing succesful");
                alert.setHeaderText("Editing of the chemistry club is done!");
                alert.setContentText("Chemistry club " + currentExperimentBeingEdited.getName() + " is edited!");

                alert.showAndWait();

                currentExperimentBeingViewed=null;


                List<String> privligesList = currentUser.getPrivileges().stream().toList();

                for (int i = 0; i < privligesList.size(); i++) {
                    if (privligesList.get(i).contains("IS_ADMIN")) {
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

            } else if (result.get() == buttonTypeTwo) {

            } else {
                currentExperimentBeingEdited=null;

                List<String> privligesList = currentUser.getPrivileges().stream().toList();

                for (int i = 0; i < privligesList.size(); i++) {
                    if (privligesList.get(i).contains("IS_ADMIN")) {
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
    public void cancelEditing() {

        currentExperimentBeingEdited = null;

        List<String> privligesList = currentUser.getPrivileges().stream().toList();

        for (int i = 0; i < privligesList.size(); i++) {
            if (privligesList.get(i).contains("IS_ADMIN")) {
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
