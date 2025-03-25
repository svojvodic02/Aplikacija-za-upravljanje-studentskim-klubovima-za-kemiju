package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class adminEditChemistryClubController {

    private List<Experiment> allExperiments=DatabaseUtils.getExperiment();

    private List<Student> allStudents=DatabaseUtils.getStudents();

    @FXML
    private TableView<ChemistryClub> chemistryClubTableView;
    @FXML
    private TableColumn<ChemistryClub,String> chemistryClubIDTableColumn;
    @FXML
    private TableColumn<ChemistryClub, String> chemistryClubNameTableColumn;
    @FXML
    private TextField chemistryClubEditNameTextField;
    @FXML
    private ListView<String> chemistryClubEditMembersListView;
    @FXML
    private ComboBox<String> chemistryClubMembersComboBox;
    @FXML
    private ListView<String> chemistryClubEditExperimentsListView;
    @FXML
    private ComboBox<String> chemistryClubExperimentsComboBox;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button addToListExperimentsButton;
    @FXML
    private Button clearListExperimentsButton;
    @FXML
    private Button addToListMembersButton;
    @FXML
    private Button clearListMembersButton;

    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @FXML
    public void initialize(){
        chemistryClubIDTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChemistryClub, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ChemistryClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getId().toString());
            }
        });
        chemistryClubNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChemistryClub, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ChemistryClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        List<ChemistryClub> allChemistryClubs= DatabaseUtils.getChemistryClub();

        ObservableList<ChemistryClub> chemistryClubObservableList= FXCollections.observableArrayList(allChemistryClubs);

        chemistryClubTableView.setItems(chemistryClubObservableList);


        setDisabledItems(false, true);

    }

    public void startEdit(){


        Optional<ChemistryClub> chemistryClubToEdit=Optional.empty();

        ChemistryClub chosenChemistryFromTable=chemistryClubTableView.getSelectionModel().getSelectedItem();

        chemistryClubToEdit= Optional.ofNullable(chosenChemistryFromTable);

        if(chemistryClubToEdit.isPresent()) {
            setDisabledItems(true,false);
            List<Experiment> listOfChosenChemistryClubExperiment = chemistryClubToEdit.get().getListOfExperiments();
            List<String> namesOfChosenClubExperiments=DatabaseUtils.getExperimentName(listOfChosenChemistryClubExperiment);
            ObservableList<String> observableListChosenClubExperiments=FXCollections.observableArrayList(namesOfChosenClubExperiments);
            chemistryClubEditExperimentsListView.setItems(observableListChosenClubExperiments);

            List<String> namesOfAllExperiments=DatabaseUtils.getExperimentName(allExperiments);
            ObservableList<String> observableListOfAllExperimentNames=FXCollections.observableArrayList(namesOfAllExperiments);
            chemistryClubExperimentsComboBox.setItems(observableListOfAllExperimentNames);

            List<Student> listOfChosenChemistryClubMembers=chemistryClubToEdit.get().getListOfStudents();
            List<String> namesOfChosenClubMembers=DatabaseUtils.getStudentName(listOfChosenChemistryClubMembers);
            ObservableList<String> observableListChosenClubMembers=FXCollections.observableArrayList(namesOfChosenClubMembers);
            chemistryClubEditMembersListView.setItems(observableListChosenClubMembers);

            List<String> namesOfAllStudents=DatabaseUtils.getStudentName(allStudents);
            ObservableList<String> observableListOfAllStudentNames=FXCollections.observableArrayList(namesOfAllStudents);
            chemistryClubMembersComboBox.setItems(observableListOfAllStudentNames);

        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose a chemistry from the table to edit!");
            alert.showAndWait();
        }

    }

    public void addNewExperimentToList(){
        addToListExperimentsButton.setOnAction((e) -> {
            chemistryClubEditExperimentsListView.getItems().add(chemistryClubExperimentsComboBox.getSelectionModel().getSelectedItem());
        });

    }
    public void clearExperimentsList(){

        List<String> emptyStringList=new ArrayList<>();
        ObservableList<String> emptyList=FXCollections.observableArrayList(emptyStringList);
        chemistryClubEditExperimentsListView.setItems(emptyList);
    }

    public void addNewMemberToList(){
        addToListMembersButton.setOnAction((e) -> {
            chemistryClubEditMembersListView.getItems().add(chemistryClubMembersComboBox.getSelectionModel().getSelectedItem());
        });
    }
    public void clearMemberList(){
        List<String> emptyStringList=new ArrayList<>();
        ObservableList<String> emptyList=FXCollections.observableArrayList(emptyStringList);
        chemistryClubEditMembersListView.setItems(emptyList);
    }

    public void saveEdit() throws SQLException, IOException {

        Optional<ChemistryClub> chemistryClubToEdit=Optional.empty();

        ChemistryClub chosenChemistryFromTable=chemistryClubTableView.getSelectionModel().getSelectedItem();

        chemistryClubToEdit= Optional.ofNullable(chosenChemistryFromTable);

        List<String> listNamesOfMembers=chemistryClubEditMembersListView.getItems();

        List<Student> editedMembersOfClub=new ArrayList<>();

        if(chemistryClubToEdit.isPresent()) {
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Are you sure you want to edit " + chemistryClubToEdit.get().getName() + " chemistry club?");
            alert1.setHeaderText(null);
            alert1.setContentText("Choose your option.");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeTwo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert1.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
            alert1.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);

            Optional<ButtonType> result = alert1.showAndWait();
            if (result.get() == buttonTypeOne) {

                for (int i = 0; i < listNamesOfMembers.size(); i++) {
                    for (int j = 0; j < allStudents.size(); j++) {
                        if (allStudents.get(j).getName().contains(listNamesOfMembers.get(i))) {
                            editedMembersOfClub.add(allStudents.get(j));
                        }
                    }
                }

                List<String> listNamesOfExperiments = chemistryClubEditExperimentsListView.getItems();

                List<Experiment> editedExperimentsOfCLub = new ArrayList<>();

                for (int i = 0; i < listNamesOfExperiments.size(); i++) {
                    for (int j = 0; j < allExperiments.size(); j++) {
                        if (allExperiments.get(j).getName().contains(listNamesOfExperiments.get(i))) {
                            editedExperimentsOfCLub.add(allExperiments.get(j));
                        }
                    }
                }

                DatabaseUtils.chemistryClubMembersToDelete(chemistryClubToEdit.get());
                DatabaseUtils.chemistryClubExperimentsToDelete(chemistryClubToEdit.get());

                if(!editedMembersOfClub.isEmpty()) {
                    if (chemistryClubToEdit.isPresent()) {
                        DatabaseUtils.saveChemistryClubMembers(editedMembersOfClub, chemistryClubToEdit.get().getId());
                    }
                }

                if(!editedExperimentsOfCLub.isEmpty()){
                    if (chemistryClubToEdit.isPresent()) {
                        DatabaseUtils.saveChemistryClubExperiments(editedExperimentsOfCLub, chemistryClubToEdit.get().getId());
                    }
                }

                String editedChemistryClubName = chemistryClubEditNameTextField.getText();

                if (!editedChemistryClubName.isEmpty()) {
                    if (chemistryClubToEdit.isPresent()) {
                        chemistryClubToEdit.get().setName(editedChemistryClubName);
                        DatabaseUtils.updateChemistryClubName(chemistryClubToEdit.get());
                    }
                }

                Set<String> privleges=new HashSet<>();

                UserSession currentUser=UserSession.getInstace("","",privleges,"");

                List<Changes<String,String>> newChangesList=new ArrayList<>();
                Changes<String,String> newChanges=new Changes<String,String>(currentUser.getUserFirstName(),chemistryClubToEdit.get().getName(),currentTime,currentDate);
                newChangesList.add(newChanges);

                FileUtils.serializeChanges(newChangesList);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Editing succesful");
                alert.setHeaderText("Editing of the chemistry club is done!");
                alert.setContentText("Chemistry club " + chemistryClubToEdit.get().getName() + " is edited!");

                alert.showAndWait();

                setDisabledItems(false,true);

            } else if (result.get() == buttonTypeTwo) {

                setDisabledItems(false,true);

            } else {
                setDisabledItems(false,true);
            }
        }




    }

    private void setDisabledItems(boolean valueOne,boolean valueTwo) {
        chemistryClubTableView.setDisable(valueOne);
        editButton.setDisable(valueOne);
        saveButton.setDisable(valueTwo);
        chemistryClubEditNameTextField.setDisable(valueTwo);
        chemistryClubEditExperimentsListView.setDisable(valueTwo);
        chemistryClubEditMembersListView.setDisable(valueTwo);
        chemistryClubExperimentsComboBox.setDisable(valueTwo);
        chemistryClubMembersComboBox.setDisable(valueTwo);
        addToListExperimentsButton.setDisable(valueTwo);
        addToListMembersButton.setDisable(valueTwo);
        clearListExperimentsButton.setDisable(valueTwo);
        clearListMembersButton.setDisable(valueTwo);
    }

}
