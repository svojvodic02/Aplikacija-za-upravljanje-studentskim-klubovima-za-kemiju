package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.events.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminAddChemistryClubController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    List<Student> allStudents=DatabaseUtils.getStudents();

    List<Experiment> allExperiments=DatabaseUtils.getExperiment();
    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @FXML
    private TextField chemistryClubNameTextField;
    @FXML
    private ListView<String> chemistryClubMembersListView;
    @FXML
    private ComboBox<String> chemistryClubMembersComboBox;
    @FXML
    private ListView<String> chemistryClubExperimentsListView;
    @FXML
    private ComboBox<String> chemistryClubExperimentsComboBox;
    @FXML
    private Button addToListExperimentsButton;
    @FXML
    private Button addToListMembersButton;

    @FXML
    public void initialize(){


        List<String> namesOfAllExperiments=DatabaseUtils.getExperimentName(allExperiments);
        ObservableList<String> observableListOfAllExperimentNames=FXCollections.observableArrayList(namesOfAllExperiments);
        chemistryClubExperimentsComboBox.setItems(observableListOfAllExperimentNames);


        List<String> namesOfAllStudents=DatabaseUtils.getStudentName(allStudents);
        ObservableList<String> observableListOfAllStudentNames=FXCollections.observableArrayList(namesOfAllStudents);
        chemistryClubMembersComboBox.setItems(observableListOfAllStudentNames);



    }
    public void addNewChemistryGroup() {

        Long chemistryClubId=DatabaseUtils.getNextChemistryClubID();

        List<String> listNamesOfMembers=chemistryClubMembersListView.getItems();
        List<Student> studentMembersOfClub=new ArrayList<>();

        for (int i = 0; i < listNamesOfMembers.size(); i++) {
            for (int j = 0; j < allStudents.size(); j++) {
                if (allStudents.get(j).getName().contains(listNamesOfMembers.get(i))) {
                    studentMembersOfClub.add(allStudents.get(j));
                        }
                    }
                }

        Set<Student> setOfStudents = new HashSet<>(studentMembersOfClub);
        studentMembersOfClub.clear();
        studentMembersOfClub.addAll(setOfStudents);


        List<String> listNamesOfExperiments = chemistryClubExperimentsListView.getItems();

        List<Experiment> experimentsOfCLub = new ArrayList<>();

        for (int i = 0; i < listNamesOfExperiments.size(); i++) {
            for (int j = 0; j < allExperiments.size(); j++) {
                if (allExperiments.get(j).getName().contains(listNamesOfExperiments.get(i))) {
                    experimentsOfCLub.add(allExperiments.get(j));
                }
            }
        }

        Set<Experiment> setOfExperiments = new HashSet<>(experimentsOfCLub);
        experimentsOfCLub.clear();
        experimentsOfCLub.addAll(setOfExperiments);

        String newChemistryClubName = chemistryClubNameTextField.getText();

        ChemistryClub chemistryClubToAdd=new ChemistryClub(newChemistryClubName,chemistryClubId,experimentsOfCLub,studentMembersOfClub);
        List<ChemistryClub> newChemistryClub=new ArrayList<>();
        newChemistryClub.add(chemistryClubToAdd);
        DatabaseUtils.saveChemistryClub(newChemistryClub);
        DatabaseUtils.saveChemistryClubExperiments(experimentsOfCLub, chemistryClubId);
        DatabaseUtils.saveChemistryClubMembers(studentMembersOfClub,chemistryClubId);

        Set<String> privleges=new HashSet<>();

        UserSession currentUser=UserSession.getInstace("","",privleges,"");

        List<Changes<String,String>> newChangesList=new ArrayList<>();
        Changes<String,String> newChanges=new Changes<String,String>(currentUser.getUserFirstName(),newChemistryClubName,currentTime,currentDate);
        newChangesList.add(newChanges);

        FileUtils.serializeChanges(newChangesList);


        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New club saved!");
        alert.setHeaderText("Creating of a new chemistry club has been successful!");
        alert.setContentText("Chemistry club " + newChemistryClubName + " has been successfully saved.");

        alert.showAndWait();

    }

    public void addNewExperimentToList(){
        addToListExperimentsButton.setOnAction((e) -> {
            chemistryClubExperimentsListView.getItems().add(chemistryClubExperimentsComboBox.getSelectionModel().getSelectedItem());
        });

    }
    public void clearExperimentsList(){

        List<String> emptyStringList=new ArrayList<>();
        ObservableList<String> emptyList=FXCollections.observableArrayList(emptyStringList);
        chemistryClubExperimentsListView.setItems(emptyList);
    }

    public void addNewMemberToList(){
        addToListMembersButton.setOnAction((e) -> {
            chemistryClubMembersListView.getItems().add(chemistryClubMembersComboBox.getSelectionModel().getSelectedItem());
        });
    }
    public void clearMemberList(){
        List<String> emptyStringList=new ArrayList<>();
        ObservableList<String> emptyList=FXCollections.observableArrayList(emptyStringList);
        chemistryClubMembersListView.setItems(emptyList);
    }

}
